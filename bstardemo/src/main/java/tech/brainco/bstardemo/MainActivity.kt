package tech.brainco.bstardemo

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.reactivex.disposables.CompositeDisposable
import tech.brainco.bstarblesdk.core.BstarSDK
import tech.brainco.bstarblesdk.core.BstarSDK.init
import tech.brainco.bstarblesdk.core.BstarSDK.scanDevices
import tech.brainco.bstarblesdk.core.BstarSDK.setHubConfig
import tech.brainco.bstarblesdk.core.Result
import tech.brainco.bstardemo.databinding.ActivityMainBinding
import tech.brainco.bstardemo.databinding.ItemMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mAdapter: RecyclerView.Adapter<ViewHolder>? = null
    private val macList: MutableList<String> = ArrayList()
    private val checkList: MutableSet<Int> = HashSet()

    private val compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BstarSDK.requestPermissions(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rv.layoutManager = LinearLayoutManager(this)
        mAdapter = object : RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val itemBinding = ItemMainBinding.inflate(
                    layoutInflater, parent, false
                )
                itemBinding.root.tag = itemBinding
                return object : ViewHolder(itemBinding.root) {}
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val binding = holder.itemView.tag as ItemMainBinding
                binding.cb.text = macList[position]
                binding.cb.isChecked = checkList.contains(position)
                binding.cb.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                    val pos = holder.bindingAdapterPosition
                    if (isChecked) {
                        checkList.add(pos)
                    } else {
                        checkList.remove(pos)
                    }
                }
            }

            override fun getItemCount(): Int {
                return macList.size
            }
        }
        binding.rv.adapter = mAdapter
        binding.btnConnect.setOnClickListener {
            if (checkList.isEmpty()) {
                Toast.makeText(this, "请选中设备", Toast.LENGTH_SHORT).show()
            } else {
                val selectedMacList: MutableList<String> = ArrayList()
                for (i in checkList) {
                    selectedMacList.add(macList[i])
                }
                val dialog = ProgressDialog(this)
                dialog.show()
                setHubConfig(selectedMacList, object : Result<List<String>> {
                    override fun onResult(result: List<String>) {
                        Timber.d("connect result %s", result)
                        dialog.dismiss()
                        val intent = Intent(this@MainActivity, DevicesActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onError(t: Throwable) {
                        Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                })
            }
        }
        binding.btnDevices.setOnClickListener { v: View? ->
            val intent = Intent(this@MainActivity, DevicesActivity::class.java)
            startActivity(intent)
        }
        binding.btn1.setOnClickListener { v: View? ->
            val dialog = ProgressDialog(this)
            dialog.show()
            init(this, Runnable {
                dialog.dismiss()
            })
        }
        binding.btn2.setOnClickListener {
            println("Bstar SDK disposed")
            BstarSDK.release()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            val dialog = ProgressDialog(this)
            dialog.show()
            scanDevices(object : Result<List<String>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResult(result: List<String>) {
                    checkList.clear()
                    macList.clear()
                    macList.addAll(result)
                    binding.btnConnect.visibility =
                        if (macList.isEmpty()) View.GONE else View.VISIBLE
                    mAdapter!!.notifyDataSetChanged()
                    dialog.dismiss()
                }

                override fun onError(t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            })
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

}
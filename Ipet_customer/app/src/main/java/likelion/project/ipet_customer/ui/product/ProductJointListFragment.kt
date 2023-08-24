package likelion.project.ipet_customer.ui.product

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.ipet_customer.R
import likelion.project.ipet_customer.databinding.FragmentProductJointListBinding
import likelion.project.ipet_customer.databinding.ItemProductCardBinding
import likelion.project.ipet_customer.model.Joint
import likelion.project.ipet_customer.ui.main.MainActivity
import java.util.EventListener

class ProductJointListFragment : Fragment() {

    lateinit var fragmentProductJointListBinding: FragmentProductJointListBinding
    lateinit var mainActivity: MainActivity
    val joinTdataList = mutableListOf<Joint>()
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentProductJointListBinding = FragmentProductJointListBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentProductJointListBinding.run {
            toolbarProductJointList.run {
                title = "공동 구매 상품 리스트"
                setNavigationIcon(R.drawable.ic_back_24dp)
            }
            db.collection("Joint")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val idx = document["jointIdx"] as Long
                        val animalType = document["jointAnimalType"] as String
                        val img = document["jointImg"] as String
                        val member = document["jointMember"] as Long
                        val price = document["jointPrice"] as String
                        val seller = document["jointSeller"] as String
                        val term = document["jointTerm"] as String
                        val text = document["jointText"] as String
                        val title = document["jointTitle"] as String
                        val totalMember = document["jointTotalMember"] as Long

                        val item = Joint(animalType, idx, img, member, price, seller, term, text, title, totalMember)
                        joinTdataList.add(item)
                        fragmentProductJointListBinding.recyclerProductJointList.adapter?.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "Error getting documents: ", exception)
                }

            recyclerProductJointList.run {
                adapter = Adapter()
                layoutManager = GridLayoutManager(context, 2)
            }
        }
        return fragmentProductJointListBinding.root
    }

    inner class Adapter: RecyclerView.Adapter<Adapter.Holder>() {
        inner class Holder(rowBinding: ItemProductCardBinding): RecyclerView.ViewHolder(rowBinding.root) {
            val imageViewCardThumbnail: ImageView
            val textViewCardTitle: TextView
            val textViewCardCost: TextView
            init {
                imageViewCardThumbnail = rowBinding.imageViewCardThumbnail
                textViewCardTitle = rowBinding.textViewCardTitle
                textViewCardCost = rowBinding.textViewCardCost
                rowBinding.root.setOnClickListener {
                    mainActivity.replaceFragment(MainActivity.PRODUCT_INFO_FRAGMENT, true, null)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val rowBinding = ItemProductCardBinding.inflate(layoutInflater)
            val holder = Holder(rowBinding)

            rowBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return holder
        }

        override fun getItemCount(): Int {
            return joinTdataList.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.textViewCardTitle.text = joinTdataList[position].jointTitle
            holder.textViewCardCost.text = "${joinTdataList[position].jointPrice}원"
        }
    }

}
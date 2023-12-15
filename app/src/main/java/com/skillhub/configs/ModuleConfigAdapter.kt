package com.skillhub.configs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.skillhub.R
import com.skillhub.modules.Module

class ModuleConfigAdapter(
    private val context: Context,
    private val allConfigs: MutableMap<Module, MutableList<ModuleConfig>>,
    private val selectedConfigs: MutableMap<Module, MutableList<ModuleConfig>>,
    private val onCheckBoxClicked: (CheckBox, ModuleConfig) -> Unit,
    private val onCheckBoxHeld: (CheckBox, ModuleConfig) -> Boolean
) : BaseExpandableListAdapter() {

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return allConfigs.values.elementAt(listPosition).elementAt(expandedListPosition).getName()
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getChildView(
        listPosition: Int, expandedListPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View {
        var view = convertView
        val expandedListText = getChild(listPosition, expandedListPosition) as String
        if (view == null) {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.list_item, null)
        }
        val expandedListCheckbox = view!!.findViewById<View>(R.id.list_item_expanded_list_item) as CheckBox
        expandedListCheckbox.text = expandedListText

        var unchecked = true
        for (config in selectedConfigs.values.elementAt(listPosition)) {
            if (config.getName() == expandedListText) {
                expandedListCheckbox.isChecked = true
                unchecked = false
            }
        }
        if (unchecked) {
            expandedListCheckbox.isChecked = false
        }
        expandedListCheckbox.setOnClickListener {
            if (expandedListCheckbox.isChecked) {
                selectedConfigs.values.elementAt(listPosition)
                    .add(allConfigs.values.elementAt(listPosition).elementAt(expandedListPosition))
            } else {
                for (i in 0 until selectedConfigs.values.elementAt(listPosition).size) {
                    if (selectedConfigs.values.elementAt(listPosition).elementAt(i).getConfigID() == allConfigs.values.elementAt(
                            listPosition
                        ).elementAt(expandedListPosition).getConfigID()
                    ) {
                        selectedConfigs.values.elementAt(listPosition).removeAt(i)
                    }
                }
            }
            onCheckBoxClicked(expandedListCheckbox, allConfigs.values.elementAt(listPosition).elementAt(expandedListPosition))
        }
        expandedListCheckbox.setOnLongClickListener {
            onCheckBoxHeld(expandedListCheckbox, allConfigs.values.elementAt(listPosition).elementAt(expandedListPosition))
        }
        return view
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return allConfigs.values.elementAt(listPosition).size
    }

    override fun getGroup(listPosition: Int): Any {
        return allConfigs.keys.elementAt(listPosition).getStub().descriptionName
    }

    override fun getGroupCount(): Int {
        return allConfigs.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getGroupView(listPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val listTitle = getGroup(listPosition) as String
        if (view == null) {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.list_group, null)
        }
        val listTitleTextView = view!!.findViewById<View>(R.id.list_group_list_title) as TextView
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = listTitle
        return view
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}
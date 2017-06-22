package com.carl_yang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.carl_yang.data.WorkExperience;
import com.carl_yang.resume.R;
import com.carl_yang.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class WorkExpAdapter extends BaseRecyclerAdapter<WorkExpAdapter.WorkExpAdapterViewHolder> {

    private List<WorkExperience> list;
    private int largeCardHeight, smallCardHeight;

    public WorkExpAdapter(Context context){
        if(list==null){
            list=new ArrayList<>();
            WorkExperience we=new WorkExperience();
            we.setCompany_name("北京美福科技有限公司");
            we.setStart_time("2017.02");
            we.setEnd_time("至今");
            we.setPostion("Android高级开发工程师&项目经理");
            we.setDepartment("产品部");
            we.setWorkachievement("1.对美术教学软件内的资源区搭建设定方案得到老板的高度认可.\n2.成为美术教学软件项目的项目经理.");
            we.setWorkcontent("1.美术教学软件android移动端开发及分析.\n2.移动办公OA系统的开发及维护.\n3.美术教学软件项目的项目经理.");
            we.setSkillicon("移动开发/Android");
            WorkExperience we1=new WorkExperience();
            we1.setCompany_name("北京第一纪信息技术有限公司");
            we1.setStart_time("2014.08");
            we1.setEnd_time("2016.12");
            we1.setPostion("移动端负责人");
            we1.setDepartment("技术部");
            we1.setWorkachievement("1.公司创业初期核心团队,包含老板在内的3人之一.将产品开发好,并上线.\n" +
                    "2.通过用户的反馈、线下调出、跟车了解业务等,完善APP的使用和出现的问题解决.\n." +
                    "3.因为本人专业电子信息科学与技术,对硬件内部原理略懂.公司内所有硬件相关的事情都由我来分析、使用和向供应商提供定制需求文档及当面沟通技术原理.\n" +
                    "4.创业初期,经常性的出差驻场直接和客户沟通软件需求,并设计原型图和晚上在酒店开发演示DEMO给予客户对我们专业水准信心.\n" +
                    "5.后期团队扩张到10人,管理并培养新人.");
            we1.setWorkcontent("1.结合硬件开发运输温度监测系统（食品、医药）,垃圾餐厨监测系统,媒体播放系统（Android电视）\n" +
                    "2.对老板接到的项目进行评估,分析实现的可能性和大致周期,讨论解决方案并搭建原型设计及后期开发工作.\n" +
                    "3.对公司的产品进行优化改良等.如最初的温度监控系统使用模式：大众手机+服务端+WIFI网关+蓝牙打印机+传感器 5个设备来实现监测系统.在上线后,脱离实验环境在实际运行环境中存在各种各样的问题,分析起来由于存在太多端导致及其困难定位问题所在.后期改方案变成定制PDA+服务端+传感器,少了几端,分析BUG出现的位置就容易多了,而且故障率也降低了,同时成本也减少了.\n" +
                    "4.公司的核心核心人员3人之一,对于公司的一些基本走向与老板进行会议讨论.");
            we1.setSkillicon("Android/需求分析/解决方案");
            list.add(we);
            list.add(we1);
        }
        largeCardHeight = DensityUtil.dip2px(context, 150);
        smallCardHeight = DensityUtil.dip2px(context, 100);
    }

    @Override
    public WorkExpAdapterViewHolder getViewHolder(View view) {
        return null;
    }

    @Override
    public WorkExpAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_recylerview, parent, false);
        WorkExpAdapterViewHolder we = new WorkExpAdapterViewHolder(v);
        return we;
    }

    @Override
    public void onBindViewHolder(WorkExpAdapterViewHolder holder, int position, boolean isItem) {
        holder.company_name.setText(list.get(position).getCompany_name());
        holder.time.setText(list.get(position).getStart_time()+"-"+list.get(position).getEnd_time());
        holder.postion.setText(list.get(position).getPostion()+"|"+list.get(position).getDepartment());
        holder.workachievement.setText(list.get(position).getWorkachievement());
        holder.workcontent.setText(list.get(position).getWorkcontent());
        holder.skillicon.setText(list.get(position).getSkillicon());
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            holder.rootView.getLayoutParams().height = position % 2 != 0 ? largeCardHeight : smallCardHeight;
        }
    }

    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }

    @Override
    public int getAdapterItemCount() {
        return list.size();
    }

    public class WorkExpAdapterViewHolder extends RecyclerView.ViewHolder {

        public TextView company_name;
        public TextView time;
        public TextView postion;
        public TextView workachievement;
        public TextView workcontent;
        public TextView skillicon;

        public View rootView;

        public WorkExpAdapterViewHolder(View itemView) {
            super(itemView);
            rootView = itemView
                    .findViewById(R.id.card_view);
            company_name = (TextView) itemView
                    .findViewById(R.id.item_recycler_view_companyname);
            time = (TextView) itemView
                    .findViewById(R.id.item_recycler_view_time);
            postion = (TextView) itemView
                    .findViewById(R.id.item_recycler_view_postion);
            workachievement = (TextView) itemView
                    .findViewById(R.id.item_recycler_view_workachievement);
            workcontent = (TextView) itemView
                    .findViewById(R.id.item_recycler_view_workcontent);
            skillicon = (TextView) itemView
                    .findViewById(R.id.item_recycler_view_skillicon);

        }
    }
}
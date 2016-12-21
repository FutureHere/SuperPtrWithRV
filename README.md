## 开始前总是要有废话的
很早之前，上拉加载下拉刷新这种交互方式一经推出，就火炸了。如果你在两三年前就接触过android开发，你一定听说过[PullToRefreshListView](https://github.com/johannilsson/android-pulltorefresh)这个开源框架，使用起来很简单，首先感谢伟大的作者开源这么优秀的作品，但是对于新手来讲，这个框架有些过于庞大了，类和方法实在太多，定制功能太复杂，并且不得不说，再使用过程中，这个框架的局限性很大，说到底他只是个ListView，当你和其他可滑动控件一起套用时，就会出现各种的问题，并且这个框架的作者已经好几年没有维护过他了。
我在git上尝试查找能替代他的家伙，万幸这个家伙被我成功发现了。虽然他的岁数也已经很大了，但到现在我仍然在使用并且也一直在维护他的代码。[android-Ultra-Pull-To-Refresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh)，同样感谢liaohuqiu贡献这么优秀的作品。我习惯叫这个框架为Ptr，至于为什么到现在我还要推荐这个框架，请听我慢慢道来：
* 首先他不是个ListView也不是个GridView，他是个ViewGroup，这意味着什么？他意味着你的整个ViewGroup都是可以下拉的，你的ViewGroup里可以装任何内容，TextView，Button，ListView，RecycleView都可以，他不在局限我只是个ListView，简单说，下拉这个动作，不绑定任何控件，他是独立的。这个优点我给满分。
* 充分抽象解耦合，我们可以定义属于我们自己的刷新样式，只要实现统一接口，定制你的样式没有那么难，典型的面向对象思想。
* 类相对较少。你说这也是个优点？我说这是个大大的优势，类少代码易读，核心类集中，修改与定制会很方便。

那么他没有缺点吗？当然有！

* 或许这也不算是个缺点，在作者公布的开源项目中，并不支持上拉加载更多功能，至于为什么，作者在他的issues里已经回复过了，大概意思是：**下拉刷新和加载更多，不是同一个层级的功能。加载更多不应该由 UltraPTR 去实现，而应该由 Content自己去实现。**没关系作者的另外一个开源库，有实现这一功能，在这篇文章中，我们把他整合在一起！
* 作者源码中的实现使用的是ListView，这里我们会换成RecycleView，毕竟要与时俱进嘛！
* 同样在滑动嵌套中，会有冲突的问题。没关系，代码中已经解决了一些。



### 看完这篇文章你会得到什么？
我不会对作者的源码进行解析，毕竟网上的轮子已经很多了，重复造轮子是可耻的。也不会把修改源码的过程进行讲解，为啥？因为很早之前就改好了，到现在已经忘了具体的过程。。。哈！Sorry，这篇文章我会尽力展示框架的结构和使用方式，帮助你更好理解Ptr的优势，思路是最重要的，代码并不重要！

# 开始干活啦

![](https://dn-mhke0kuv.qbox.me/8a027f911b2f08851fe7.gif)

gif中我展示了上拉加载，下拉刷新，标准，全部功能，空View五种情况。这里只是为了展示，其实我们在使用过程中，基本不会用到各种情况之间的互相切换。项目中目前只定义了两种样式的头部，第一种就是类似gif中MaterialDesign的样式，第二种就是传统样式类似PullToRefreshListView中的，这里没有展示。

### 一、使用指南
#### 1.布局使用

```
 <com.leinyo.superptrwithrv.widget.ptr.PullToRefreshView
        android:id="@+id/pull_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:ptr_refresh_mode="none"
        app:ptr_scrollable="true"
        />
```
此处不废话，讲解属性含义：

```
<declare-styleable name="PullToRefreshView">
        <attr name="ptr_refresh_mode" format="enum">
            <enum name="none" value="0"/>
            <enum name="pull_from_start" value="1"/>
            <enum name="pull_from_end" value="2"/>
            <enum name="both" value="3"/>
        </attr>
        <attr name="ptr_check_login" format="boolean"/>
        <attr name="ptr_header_mode" format="enum">
            <enum name="material" value="0"/>
            <enum name="normal" value="1"/>
        </attr>
        <attr name="ptr_padding_left" format="integer"/>
        <attr name="ptr_padding_right" format="integer"/>
        <attr name="ptr_scrollable" format="boolean"/>
    </declare-styleable>
```
* ptr_refresh_mode 刷新方式 
  none               标准的RecycleView   
  pull_from_start    支持下拉刷新
  pull_from_end      支持上拉加载
  both               上拉加载下拉刷新
* ptr_check_login 是否检查登录状态 只有登录（这部分代码自己实现）才会触发下拉刷新功能
* ptr_header_mode 下拉刷新头样式 
  material gif中演示的样式
  normal   标准模式
* ptr_padding_left ptr_padding_right 设置RecycleView的padding
* ptr_scrollable 是否显示滚动条

#### 2.刷新动作对应回调
下拉刷新对应回调接口：

```
public interface OnPullRefreshListener {
        void onPullRefresh();
    }
```
onPullRefresh()方法会在刷新动画达到临界值以后回调。
取消下拉动画方法：

```
 public void onPullRefreshComplete() {
        mPtrFrameLayout.refreshComplete();
    }

```
上拉加载对应回调接口：

```
public interface OnLoadMoreListener {
        void onLoadMoreRefresh();
    }

```
取消上拉动画方法：

```
    public void onLoadMoreComplete(boolean hasMore) {
        mLoadMoreRecyclerViewContainer.loadMoreFinish(hasMore);
    }
```
**boolean值 代表是否还可以继续上拉 false 不会再回调onLoadMoreRefresh()！！！**

both对应回调接口：

```
 public interface OnRefreshListener {
        void onPullRefresh();

        void onLoadMoreRefresh();
    }
```
取消全部动画方法：
```
  public void onLoadComplete(boolean hasMore) {
        mLoadMoreRecyclerViewContainer.loadMoreFinish(hasMore);
        if (mCurrentRefreshMode == REFRESH_FROM_START) {
            if (isRefreshing()) {
                mPtrFrameLayout.refreshComplete();
            }
        }
    }
```
#### 3.添加EmptyView
如果我们的返回的数据是空的，需要显示一个空页面。我们不需要控制两个View的show与gone，ListView能做的我们同样能到。
`   mPullView.addEmptyView(mEmpty);
`
注意：addEmptyView方法不能在显示之前设置，否则会先显示空View。为啥，后面再说。
#### 4.添加头HeadView
同样ListView能做的我们也可以。
`
mPullView.addHeaderView(mHeadView);
`


### 二、架构层次
这一部分一定是要上图的，说多少都是无意的。

![](https://dn-mhke0kuv.qbox.me/7d12a475704e24d43e80.png)

**这部分你最好对Ptr有了解**
* PtrFrameLayout 
  最外层ViewGroup，只负责下拉刷新，首先接触到触摸事件，符合下拉逻辑，则显示头布局，否则向下分发事件
* LoadMoreRecyclerViewContainer
  第二层ViewGroup，只负责上拉加载，监听上拉事件，符合逻辑，通知RecyclerView绘制Foot布局。
* RecyclerView
  最后一层，我只负责显示布局，包括Head，EmptyView，Foot和正常的数据布局。

怎么样？一张图是不是已经足够清楚了？看到现在你是否已经感叹作者的设计能力了？再看看上面说到作者在issues里已经回复的：**下拉刷新和加载更多，不是同一个层级的功能。加载更多不应该由 UltraPTR 去实现，而应该由 Content自己去实现。**
每一层级，只对应自己的业务逻辑，并不关心其他人在干什么，这就叫单一职责，解耦合。

**本来觉得这里应该是讲得最多的，但是写到这里，发现实在没啥可说的了，如果你真的看过Ptr的源码，相信到这里你也已经没有疑惑了，而且有种神清气爽的感觉，其实就是这么简单，代码设计很重要，这也是为啥有的代码让人看得很爽，有的代码让人看了想吐**

### 三、显示布局
我们上面说过真正负责显示布局的是RecyclerView了，怎么显示？当然是交给Adapter就可以了，多布局显示使用RecyclerView.Adapter的getItemViewType(int position)。需要记住的是：
**我们抽出基类BaseRefreshAdapter，里面对多布局显示有一些基础的操作，需要子类继承我们抽出基类BaseRefreshAdapter，并实现onCreateHolder(ViewGroup parent, int viewType)(他等同于onCreateViewHolder(ViewGroup parent, int viewType))和onBindHolder(VH holder, int position)（等同于onBindViewHolder(RecyclerView.ViewHolder holder, int position))，在子类中你只需要关心你自己的Item布局就可以了，其他交给BaseRefreshAdapter中的逻辑就可以了。**
这里面有泛型定义，看看源码很好理解。
这就不难理解为啥上面说过addEmptyView方法不能在显示布局之前设置了，因为如果你初始化就执行addEmptyView方法，那么当adapter初始化时，就会执行onCreateViewHolder()一系列方法，这样马上就会显示EmptyView了，等你的真正需要的数据返回才会刷新数据布局。

## 最后也还是要有些废话的
本人并未侧重讲解怎么封装，怎么修改源码适配RecyclerView，也并未拆源码讲解Ptr原理，也希望大家能理解，时间有点紧，而且轮子很多，不想再造了。
我希望如果你看到最后，这篇文章会对你有所帮助，学习人家的设计模式的同时，你也得到了一个支持多种功能，基于RecyclerView的再封装Ptr框架（感觉好绕口啊），至于为何叫SuperPtr，纯碎是为了好玩。。。
如果您在使用过程中，有疑问和改进意见欢迎联系我。
[SupterPtr](https://github.com/FutureHere/SuperPtrWithRV) 对您的支持我深表感谢，喜欢请您star哦~~

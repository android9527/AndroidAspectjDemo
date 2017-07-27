#### AOP概念

百度百科中对AOP的解释如下:
在软件业，AOP为Aspect Oriented Programming的缩写，意为：面向切面编程，通过预编译方式和运行期动态代理实现程序功能的统一维护的一种技术。AOP是OOP的延续，是软件开发中的一个热点，也是Spring框架中的一个重要内容，是函数式编程的一种衍生范型。 <!--more-->利用AOP可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。

说白了，AOP其实就是OOP的补充，OOP从横向上区分出一个个的类来，而AOP则从纵向上向对象中加入特定的代码。

#### AOP 和 OOP

面向对象的特点是继承、多态和封装。为了符合单一职责的原则，OOP将功能分散到不同的对象中去。让不同的类设计不同的方法，这样代码就分散到一个个的类中。可以降低代码的复杂程度，提高类的复用性。

但是在分散代码的同时，也增加了代码的重复性。比如说，我们在两个类中，可能都需要在每个方法中做日志。按照OOP的设计方法，我们就必须在两个类的方法中都加入日志的内容。也许他们是完全相同的，但是因为OOP的设计让类与类之间无法联系，而不能将这些重复的代码统一起来。然而AOP就是为了解决这类问题而产生的，它是在运行时动态地将代码切入到类的指定方法、指定位置上的编程思想。

如果说，面向过程的编程是一维的，那么面向对象的编程就是二维的。OOP从横向上区分出一个个的类，相比过程式增加了一个维度。而面向切面结合面向对象编程是三维的，相比单单的面向对象编程则又增加了“方面”的维度。从技术上来说，AOP基本上是通过代理机制实现的。

#### 在Android项目中使用AspectJ

本文采用上海沪江公司徐宜生团队开源的gradle插件
以下引用部分原文

AOP的用处非常广，从spring到Android，各个地方都有使用，特别是在后端，Spring中已经使用的非常方便了，而且功能非常强大，但是在Android中，AspectJ的实现是略阉割的版本，并不是所有功能都支持，但对于一般的客户端开发来说，已经完全足够用了。

在Android上集成AspectJ实际上是比较复杂的，不是一句话就能compile，但是，鄙司已经给大家把这个问题解决了，大家现在直接使用这个SDK就可以很方便的在Android Studio中使用AspectJ了。Github地址如下：
https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx
另外一个比较成功的使用AOP的库是JakeWharton大神的Hugo：
https://github.com/JakeWharton/hugo

#### 接入说明

 * 首先，需要在项目根目录的build.gradle中增加依赖：
完整代码如下：
```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.3'
        classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:1.0.10'

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
```
 * 然后再主项目或者库的build.gradle中增加AspectJ的依赖：
```
compile 'org.aspectj:aspectjrt:1.8.10'
```

 * 同时在build.gradle中加入AspectJX模块：
```
apply plugin: 'android-aspectjx'
```
 这样就把整个Android Studio中的AspectJ的环境配置完毕了，如果在编译的时候，遇到一些『can’t determine superclass of missing type xxxxx』这样的错误，请参考项目README中关于excludeJarFilter的使用。
```
aspectjx {
    //includes the libs that you want to weave
    includeJarFilter 'universal-image-loader', 'AspectJX-Demo/library'

    //excludes the libs that you don't want to weave
    excludeJarFilter 'universal-image-loader'
}
```
运行时如果出现`Task 'transformClassesWithExtractJarsForDebug' not found in project ':app'.`的错误,需要关闭Instant Run功能
https://github.com/HujiangTechnology/AspectJX-Demo/issues/1

具体配置参见github地址 https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx



#### AspectJ入门

我们通过一段简单的代码来了解下基本的使用方法和功能，新建一个AspectTest类文件，代码如下：

```java
@Aspect
public class AspectTest {

    private static final String TAG = "xuyisheng";

    @Before("execution(* android.app.Activity.on**(..))")
    public void onActivityMethodBefore(JoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toString();
        Log.d(TAG, "onActivityMethodBefore: " + key);
    }
}
```
在类的最开始，我们使用@Aspect注解来定义这样一个AspectJ文件，编译器在编译的时候，就会自动去解析，并不需要主动去调用AspectJ类里面的代码。

我的原始代码很简单：

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
```
通过这种方式编译后，我们来看下生成的代码是怎样的。AspectJ的原理实际上是在编译的时候，根据一定的规则解析，然后插入一些代码，通过aspectjx生成的代码，会在Build目录下：
 ![aspectj-1](/images/aspectj/aspectj1.jpeg)

通过反编译工具查看下生成内容：

我们可以发现，在onCreate的最前面，插入了一行AspectJ的代码。这个就是AspectJ的主要功能，抛开AOP的思想来说，我们想做的，实际上就是『在不侵入原有代码的基础上，增加新的代码』。

#### 使用AOP防止按钮连续点击
 * 首先定义一个防止多次点击的工具类
 ```java
 public class NoDoubleClickUtils {
     private static long lastClickTime = 0;
     private static final int SPACE_TIME = 500;
 
     public synchronized static boolean isDoubleClick() {
         long currentTime = System.currentTimeMillis();
         boolean isClick2 = currentTime - lastClickTime <= SPACE_TIME;
         lastClickTime = currentTime;
         return isClick2;
     }
 }
 ```
 * 然后使用AspectJ对OnclickListener进行插桩，
```java
@Around("execution(* android.view.View.OnClickListener.onClick(..))")
public void onClickListener(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    Log.e(TAG, "OnClick");
    if (!NoDoubleClickUtils.isDoubleClick()) {
        proceedingJoinPoint.proceed();
    }
}
```
 * 运行程序，多次点击按钮后，log如下
```
07-27 12:38:37.789 24084-24084/com.example.androidaspectjdemo E/AspectTest: OnClick
07-27 12:38:37.789 24084-24084/com.example.androidaspectjdemo E/MainActivity: execute click
07-27 12:38:38.053 24084-24084/com.example.androidaspectjdemo E/AspectTest: OnClick
07-27 12:38:38.290 24084-24084/com.example.androidaspectjdemo E/AspectTest: OnClick
07-27 12:38:38.538 24084-24084/com.example.androidaspectjdemo E/AspectTest: OnClick
07-27 12:38:38.771 24084-24084/com.example.androidaspectjdemo E/AspectTest: OnClick
07-27 12:38:39.006 24084-24084/com.example.androidaspectjdemo E/AspectTest: OnClick
07-27 12:38:39.257 24084-24084/com.example.androidaspectjdemo E/AspectTest: OnClick
```

通过log可以看出onClickLitener执行了多次，但使用clcik的的地方只执行了一次。这样，就可以在不改变原来代码的情况下，实现防止连续点击的功能。

 * 但是当又有需求：要求部分按钮是可以连续点击的，该怎么办能？这个时候只要加个注解文件就好。

 * 首先定义个注解
```java
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface DoubleClick {
}
```
 * 并且修改之前的AspectTest文件
```java
    private View mLastView;
    private boolean canDoubleClick = false;

    @Before("@annotation(com.example.spectjde.annotation.DoubleClick)")
    public void beforeEnableDoubleClick(JoinPoint joinPoint) throws Throwable {
        canDoubleClick = true;
    }

    @Around("execution(* android.view.View.OnClickListener.onClick(..))  && target(Object) && this(Object)")
    public void onDoubleClickListener(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] objects = joinPoint.getArgs();
        View view = objects.length == 0 ? null : (View) objects[0];
        if (view != mLastView || canDoubleClick || !NoDoubleClickUtils.isDoubleClick()) {
            joinPoint.proceed();
            canDoubleClick = false;
        }
        mLastView = view;
    }
```

现在只要在可以连续点击的按钮的onclick前加一个@DoubleClick的注解就好

#### 使用注解实现方法运行在异步线程

 * 首先定义注解作用在Method上
```java
 @Target({METHOD})
 @Retention(CLASS)
 public @interface Async {
 }
```

 * 创建AsyncAspect.java
```java
@Aspect 
public class AsyncAspect {

    @Around("execution(!synthetic * *(..)) && onAsyncMethod()")
    public void doAsyncMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        asyncMethod(joinPoint);
    }

    @Pointcut("@within(com.example.spectjde.annotation.Async)||@annotation(com.example.spectjde.annotation.Async)")
    public void onAsyncMethod() {
    }

    private void asyncMethod(final ProceedingJoinPoint joinPoint) throws Throwable {

        // 使用Rxjava实现线程切换
        Flowable.create(new FlowableOnSubscribe<Object>() {
                            @Override
                            public void subscribe(FlowableEmitter<Object> e) throws Exception {
                                try {
                                    joinPoint.proceed();
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }
                        }
                , BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
```
 * 在要切换的方法上加上注解即可
```java
@Async
void testAsync() {
    Log.e(TAG, Thread.currentThread().getName());
}
```

#### 实现所有方法耗时统计
 * 创建Aspect class
```java
@Aspect
public class AspectJSpectControler {
    private static final String TAG = AspectJSpectControler.class.getSimpleName();

    @Around(value = "execution(* com.example..*.*(..))")
    public Object weavePatchLogic(ProceedingJoinPoint joinPoint) throws Throwable {
        if (BuildConfig.DEBUG) { //debug    状态下计算方法耗时
            long startT = System.currentTimeMillis();
            Object proceed = joinPoint.proceed();
            long consume = System.currentTimeMillis() - startT;
            Log.d(TAG, "AspectJSpectControler: " + consume + " ms " + joinPoint.getSignature());
            return proceed;
        }
        return joinPoint.proceed();
    }
}
```
 * 运行app，过滤log查看方法耗时,打印log过滤关键字
```
D/AspectTest: AspectJSpectControler: 127 ms void com.example.androidaspectjdemo.MainActivity.onCreate(Bundle)
D/AspectTest: AspectJSpectControler: 20 ms void com.example.spectjde.AsyncAspect.asyncMethod
```

Demo地址  https://github.com/android9527/AndroidAspectjDemo

#### 参考资料：

[Android防止按钮连续点击方案之AOP](http://www.cnblogs.com/yxx123/p/6675567.html)

[归纳AOP在Android开发中的几种常见用法](http://www.jianshu.com/p/2779e3bb1f14)

[看AspectJ在Android中的强势插入](http://blog.csdn.net/eclipsexys/article/details/54425414)

[AOP技术在网易新闻中的应用](http://glanwang.com/2017/07/18/Android/AOP%E6%8A%80%E6%9C%AF%E5%9C%A8%E7%BD%91%E6%98%93%E6%96%B0%E9%97%BB%E4%B8%AD%E7%9A%84%E5%BA%94%E7%94%A8/)



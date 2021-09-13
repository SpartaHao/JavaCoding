策略模式，汇总map的两种实现方式：
## 1、通过ApplicationContextAware的setApplicationContext方法设置上下文
````javascript
// 这种就是利用spring的一些特性，在初始化之前自动将所有的实现类加载到map里，比较优雅
@Component
public class RestControllerFactory implements ApplicationContextAware {
    private static Map<String, IResourceController> serviceMap = new HashMap<>();

    public static IResourceController getDefaultServiceClient() {
        return serviceMap
                .get(StringUtil.lowFirstChar
                        (SimpleResourceControllerImpl.class.getSimpleName()));
    }

    // 根据模式获取具体的实现类
    public static IResourceController getSpecialControllerByMode(String mode) {
        switch (mode) {
            case "simple":

                return serviceMap
                        .get(StringUtil.lowFirstChar(ComplexResourceControllerImpl.class.getSimpleName()));
            case "complex":

                return serviceMap
                        .get(StringUtil.lowFirstChar(SimpleResourceControllerImpl.class.getSimpleName()));
            default:
                return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        // 通过容器取得实现接口所有的类，统一放到map里
        RestControllerFactory.serviceMap.putAll(applicationContext.getBeansOfType
                (IResourceController.class));
    }
}
````

## 2、也是通过spring，在父类的构造函数统一进行处理

````javascript
// 父类
public abstract class ParentHand<T extends ParentParam> {
    public static final HashMap<String, Class> CHILD_CLASS_MAP = new HashMap<>(32);
    private static final String POINT_STR = "\\.";

    public abstract HttpResponse hand(T p);
    
    // 父类构造函数，spring是通过构造函数实例化对象的，子类构造函数执行之前，会先执行父类的构造函数
    protected ParentHand(){
        Class handCls = this.getClass();
        String[] names = handCls.getTypeName().split(POINT_STR);
        String className = names[names.length-1];
        className = className.substring(0, 1).toLowerCase(Locale.ROOT) + className.substring(1);
        CHILD_CLASS_MAP.put(className, handCls);       
    }
}

// 子类示例
// 在调用子类构造器之前，会先调用父类构造器，当子类构造器中没有使用"super(参数或无参数)"指定调用父类构造器时，是默认调用父类的无参构造器，
如果父类中包含有参构造器，却没有无参构造器，则在子类构造器中一定要使用“super(参数)”指定调用父类的有参构造器，不然就会报错。

@Slf4j
@Component
public class childHand1 extends ParentHand<ChildParam> {
    @Override
    public HttpResponse hand(ChildParam param) {
        // TODO do something
    }
}
````

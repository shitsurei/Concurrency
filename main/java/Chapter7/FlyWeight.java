package Chapter7;

/**
 * 02
 * 享元模式：当需要重用数量有限的同一对象时
 * 例如：
 * 1 包装类（valueOf方法）
 * 类初始化时创建缓存数组，当使用值在一定范围内的对象时直接从缓存数组中取，从而避免创建大量对象
 * Byte，Short和Long的缓存数组范围都是[-128,127]
 * Character的缓存数组范围是[0,127]
 * Integer的缓存数组范围是[-128,127]，最小值不变，最大值可以通过虚拟机参数设置
 * Boolean缓存了True和False两种
 *
 * 2 String字符串池
 *
 * 3 BigDecimal和BigInteger
 */
public class FlyWeight {
}

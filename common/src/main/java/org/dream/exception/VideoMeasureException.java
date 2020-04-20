package org.dream.exception;

public class VideoMeasureException extends Exception{

    //关于默认空参构造方法的注意事项：
    //当我们没有书写任何构造方法时（空参构造或者有参构造），jvm会默认隐式的创建一个空参构造
    //当我们书写了一个有参构造时，jvm将不会再隐式的创建空参构造，此时当前类有且仅有一个我们书写的有参构造函数

    //建议：当我们创建有参构造时，把空参构造也加上，这样该类的子类空参构造才会正常(子类的空参构造，隐式调用父类的空参构造函数)

    public VideoMeasureException() {
        super();
    }

    public VideoMeasureException(String message) {
        super(message);
    }

    public VideoMeasureException(Throwable cause) {
        super(cause);
    }
}

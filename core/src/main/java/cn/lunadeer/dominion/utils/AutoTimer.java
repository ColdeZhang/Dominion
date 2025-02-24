package cn.lunadeer.dominion.utils;

/**
 * 自动计时器，用于计算代码段的耗时
 * 使用方法：
 * try (AutoTimer timer = new AutoTimer()) {
 * // 要计时的代码段
 * // ...
 * }
 * 代码块结束时自动计算耗时并打印
 */
public class AutoTimer implements AutoCloseable {

    private long startTime;
    private String callerInfo;
    private final boolean enable;

    public AutoTimer(boolean enable) {
        this.enable = enable;
        if (!enable) return;
        startTime = System.nanoTime();
        callerInfo = getCallerInfo();
    }

    // 构造函数，创建对象时自动开始计时
    public AutoTimer() {
        this(true);
    }

    // 获取调用AutoTimer的函数名和代码位置
    private String getCallerInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // stackTrace[0]是getStackTrace方法, [1]是getCallerInfo方法, [2]是构造方法, [3]是调用构造方法的地方
        StackTraceElement element = stackTrace[3];
        return "Caller: " + element.getClassName() + "." + element.getMethodName() +
                " (" + element.getFileName() + ":" + element.getLineNumber() + ")";
    }

    // 关闭方法，计算并打印耗时
    @Override
    public void close() {
        if (!enable) return;
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double durationInMillis = duration / 1_000_000.0;   // 纳秒转毫秒
        XLogger.info("%s TimeSpan: %.3f ms (%d ns)", callerInfo, durationInMillis, duration);
    }
}

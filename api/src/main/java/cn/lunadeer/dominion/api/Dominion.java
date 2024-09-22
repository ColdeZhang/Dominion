package cn.lunadeer.dominion.api;

public interface Dominion {

    static Cache getInstance() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // Cache.instance is a static field in the Cache class
        return (Cache) Class.forName("cn.lunadeer.dominion.Cache").getDeclaredField("instance").get(null);
    }
}

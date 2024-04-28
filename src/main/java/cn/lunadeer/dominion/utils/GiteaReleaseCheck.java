package cn.lunadeer.dominion.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class GiteaReleaseCheck {
    private static class GiteaRelease {
        public String tag_name;
        public String message;
        public String html_url;
        public String download_url;
    }

    public GiteaReleaseCheck(JavaPlugin plugin, String giteaServer, String owner, String repo) {
        this.gitea_server = giteaServer;
        this.owner = owner;
        this.repo = repo;
        this.plugin = plugin;
        this.current_version = plugin.getPluginMeta().getVersion();
        // 异步每12小时检查一次更新
        plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, (instance) -> {
            getLatestRelease();
            if (auto_update) {
                downloadUpdate();
            }
        }, 10, 60 * 60 * 12, TimeUnit.SECONDS);
    }

    public void enableAutoUpdate() {
        auto_update = true;
    }

    private String repoReleases() {
        return gitea_server + "/api/v1/repos/" + owner + "/" + repo + "/releases";
    }

    private String tag(String tagName) {
        return gitea_server + "/api/v1/repos/" + owner + "/" + repo + "/tags/" + tagName;
    }

    private void getLatestRelease() {
        XLogger.info("================================");
        XLogger.info("正在检查更新...");
        // send get request to repoReleases()
        try {
            // 发送 GET 请求
            HttpsURLConnection connection = (HttpsURLConnection) new URL(repoReleases()).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            // 获取响应
            StringBuilder builder = new StringBuilder();
            try (BufferedReader bufferedReader =
                         new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
            }
            JSONArray releaseList = (JSONArray) new JSONParser().parse(builder.toString());
            JSONObject latestRelease = (JSONObject) releaseList.get(0);
            GiteaRelease release = new GiteaRelease();
            release.tag_name = (String) latestRelease.get("tag_name");
            release.message = (String) latestRelease.get("body");
            release.html_url = (String) latestRelease.get("html_url");
            JSONArray assets = (JSONArray) latestRelease.get("assets");
            if (assets.size() > 0) {
                JSONObject asset = (JSONObject) assets.get(0);
                release.download_url = (String) asset.get("browser_download_url");
            }
            latest_release = release;
            XLogger.debug("Latest release: " + latest_release.tag_name);
            XLogger.debug("Message: " + latest_release.message);
            XLogger.debug("Download URL: " + latest_release.download_url);
            XLogger.debug("HTML URL: " + latest_release.html_url);
            if (isNewVersion(current_version, latest_release.tag_name)) {
                XLogger.info("发现新版本：" + latest_release.tag_name);
                XLogger.info("版本信息：");
                String[] message = latest_release.message.split("\n");
                for (String line : message) {
                    XLogger.info("\t" + line);
                }
                XLogger.info("下载页面：" + latest_release.html_url);
            } else {
                XLogger.info("当前已是最新版本：" + current_version);
            }
            XLogger.info("================================");
        } catch (Exception e) {
            XLogger.err("Failed to get latest release: " + e.getMessage());
        }
    }

    private String getTagMessage(String tagName) {
        try {
            // 发送 GET 请求
            HttpsURLConnection connection = (HttpsURLConnection) new URL(tag(tagName)).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            // 获取响应
            StringBuilder builder = new StringBuilder();
            try (BufferedReader bufferedReader =
                         new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
            }
            JSONObject tag = (JSONObject) new JSONParser().parse(builder.toString());
            return (String) tag.get("message");
        } catch (Exception e) {
            XLogger.debug("Failed to get tag message: " + e.getMessage());
            return "null";
        }
    }

    private void downloadUpdate() {
        if (latest_release == null) {
            getLatestRelease();
            if (latest_release == null)
                return;
        }
        if (!isNewVersion(current_version, latest_release.tag_name)) {
            XLogger.info("当前已是最新版本");
            return;
        }
        if (latest_release.download_url == null) {
            XLogger.err("下载地址不可用");
            return;
        }
        try {
            XLogger.info("================================");
            XLogger.info("正在下载更新...");
            File pluginsFolder = plugin.getDataFolder().getParentFile();
            File newJarFile = new File(pluginsFolder, latest_release.download_url.substring(latest_release.download_url.lastIndexOf("/") + 1));
            // send get request to download_url
            HttpsURLConnection connection = (HttpsURLConnection) new URL(latest_release.download_url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            // 获取响应写入文件到 newJarFile
            try (DataOutputStream outputStream = new DataOutputStream(Files.newOutputStream(newJarFile.toPath()))) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = connection.getInputStream().read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
            }
            XLogger.info("更新下载完成");
            XLogger.info("新版本：" + latest_release.tag_name);
            XLogger.info("请删除旧版本插件，然后重启服务器。");
            XLogger.info("================================");
        } catch (Exception e) {
            XLogger.err("Failed to auto update: " + e.getMessage());
        }
    }

    private String gitea_server;
    private String owner;
    private String repo;
    private JavaPlugin plugin;
    private String current_version;
    private GiteaRelease latest_release = null;
    private boolean auto_update = false;

    private boolean isNewVersion(String current, String in_coming) {
        // 只保留数字和点号
        current = current.replaceAll("[^0-9.]", "");
        in_coming = in_coming.replaceAll("[^0-9.]", "");
        XLogger.debug("Current version: " + current);
        XLogger.debug("In-coming version: " + in_coming);
        String[] current_version = current.split("\\.");
        String[] in_coming_version = in_coming.split("\\.");
        for (int i = 0; i < Math.min(current_version.length, in_coming_version.length); i++) {
            int current_v = Integer.parseInt(current_version[i]);
            int in_coming_v = Integer.parseInt(in_coming_version[i]);
            if (current_v < in_coming_v) {
                return true;
            } else if (current_v > in_coming_v) {
                return false;
            }
        }
        return current_version.length < in_coming_version.length;
    }
}

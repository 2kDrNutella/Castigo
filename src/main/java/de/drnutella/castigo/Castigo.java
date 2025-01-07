package de.drnutella.castigo;

import de.drnutella.castigo.commands.CustomPunishCommand;
import de.drnutella.castigo.commands.template.TemplateListCommand;
import de.drnutella.castigo.commands.template.TemplatePunishCommand;
import de.drnutella.castigo.commands.UnPunishCommand;
import de.drnutella.castigo.listener.PlayerChatListener;
import de.drnutella.castigo.listener.PlayerJoinListener;
import de.drnutella.castigo.listener.PlayerQuitListener;
import de.drnutella.castigo.mysql.MySQL;
import de.drnutella.castigo.mysql.api.DatabaseManager;
import de.drnutella.castigo.mysql.api.punish.PunishDataAdapter;
import de.drnutella.castigo.mysql.api.user.UserDataAdapter;
import de.drnutella.castigo.utils.JSONFileBuilder;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Castigo extends Plugin {

    static Castigo instance;
    static MySQL mySQL;
    static final int NETTY_PORT = 25500;

    static ExecutorService executorService;
    static DatabaseManager databaseManager;
    static UserDataAdapter userDataAdapter;
    static PunishDataAdapter punishDataAdapter;

    final PluginManager pluginManager = getProxy().getPluginManager();
    static final JSONFileBuilder configJSONHandler = new JSONFileBuilder();
    static final JSONFileBuilder mysqlJSONHandler = new JSONFileBuilder();
    static final JSONFileBuilder templateJSONHandler = new JSONFileBuilder();

    @Override
    public void onEnable() {
        instance = this;
        executorService = Executors.newCachedThreadPool();

       // startNettyServer();

        fileCreation();
        registerCommands();
        registerListener();
        loadDatabase();

        getLogger().info("§6Castigo §7(§e" + getDescription().getVersion() + "§7) §asuccessfully §a§llaunched!");
    }

    @Override
    public void onDisable() {
        mySQL.closePool();
        executorService.shutdown();
        getLogger().info("§6Castigo §7(§e" + getDescription().getVersion() + "§7) §csuccessfully §c§ldisabled!");
    }

    void loadDatabase() {
        databaseManager = new DatabaseManager();
        databaseManager.createDefaultTables();

        userDataAdapter = new UserDataAdapter(executorService, mySQL);
        punishDataAdapter = new PunishDataAdapter(executorService, mySQL);
    }

    void fileCreation() {
        configJSONHandler.createJsonFileFromTemplate("plugins/Castigo", "config", "config");
        configJSONHandler.setDefaultValues("plugins/Castigo", "config");

        mysqlJSONHandler.createJsonFileFromTemplate("plugins/Castigo", "mysql", "mysql");
        mysqlJSONHandler.setDefaultValues("plugins/Castigo", "mysql");

        templateJSONHandler.createJsonFileFromTemplate("plugins/Castigo", "templates", "templates");
        templateJSONHandler.setDefaultValues("plugins/Castigo", "templates");

        mySQL = new MySQL();
    }

    void registerListener() {
        pluginManager.registerListener(this, new PlayerJoinListener());
        pluginManager.registerListener(this, new PlayerQuitListener());
        pluginManager.registerListener(this, new PlayerChatListener());
    }

    void registerCommands() {
        pluginManager.registerCommand(this, new TemplatePunishCommand("ban", "castigo.ban"));
        pluginManager.registerCommand(this, new TemplatePunishCommand("mute", "castigo.mute"));
        pluginManager.registerCommand(this, new UnPunishCommand("unban", "castigo.unban"));
        pluginManager.registerCommand(this, new UnPunishCommand("unmute", "castigo.unmute"));
        pluginManager.registerCommand(this, new TemplateListCommand("bantemplates", "castigo.bantemplates"));
        pluginManager.registerCommand(this, new TemplateListCommand("mutetemplates", "castigo.mutetemplates"));
        pluginManager.registerCommand(this, new CustomPunishCommand("customban", "castigo.customban"));
        pluginManager.registerCommand(this, new CustomPunishCommand("custommute", "castigo.custommute"));
    }

/* Base for Spring Implementation
    void startNettyServer() {
        new Thread(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(
                                        new io.netty.handler.codec.string.StringDecoder(StandardCharsets.UTF_8),
                                        new io.netty.handler.codec.string.StringEncoder(StandardCharsets.UTF_8),
                                        new SimpleChannelInboundHandler<String>() {
                                            @Override
                                            protected void channelRead0(ChannelHandlerContext ctx, String message) {
                                                getLogger().info("Empfangene Nachricht: " + message);

                                                // Beispiel-Antwort an den Client
                                                ctx.writeAndFlush(handleNettyMessage(message.trim()));
                                            }

                                            @Override
                                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                                cause.printStackTrace();
                                                ctx.close();
                                            }
                                        });
                            }
                        });

                ChannelFuture future = bootstrap.bind(NETTY_PORT).sync();
                getLogger().info("Netty Server gestartet auf Port " + NETTY_PORT);
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                getLogger().severe("Fehler beim Start des Netty Servers: " + e.getMessage());
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }).start();
    }

    private String handleNettyMessage(String message) {
        if (message.startsWith("KICK:")) {
            String playerName = message.substring(5).trim();
            ProxyServer.getInstance().getPlayer(playerName).disconnect("Kicked By Webpanel");
            return "SUCCESS";
        } else if (message.startsWith("BROADCAST:")) {
            String broadcastMessage = message.substring(10).trim();
            return "SUCCESS";
        }else if (message.equalsIgnoreCase("PLAYERCOUNT")) {
            return String.valueOf(ProxyServer.getInstance().getPlayers().size());
        } else {
            getLogger().info("Unbekannte Nachricht empfangen: " + message);
            return "FAILED";
        }
    }
 */

    public static Castigo getInstance() {
        return instance;
    }

    public static JSONFileBuilder getConfigJSONHandler() {
        return configJSONHandler;
    }

    public static JSONFileBuilder getMysqlJSONHandler() {
        return mysqlJSONHandler;
    }

    public static JSONFileBuilder getTemplateJSONHandler(){
        return templateJSONHandler;
    }

    public static ExecutorService getOwnExecutorService() {
        return executorService;
    }

    public static UserDataAdapter getUserDatabaseManager() {
        return userDataAdapter;
    }

    public static PunishDataAdapter getPunishDatabaseManager() {
        return punishDataAdapter;
    }

    public static MySQL getMySQL() {
        return mySQL;
    }
}

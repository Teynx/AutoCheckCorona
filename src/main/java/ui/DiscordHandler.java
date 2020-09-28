package ui;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.security.auth.login.LoginException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DiscordHandler {

    public static boolean debug = false;
    private static JDA jda = null;
    private static TextChannel textChannel;
    private static SimpleDateFormat formatter = new SimpleDateFormat("M/W HH:mm");

    public void deleteAllMessage(Boolean force){
        List<Message> messages = textChannel.getHistory().retrievePast(100).complete();
        if(messages.isEmpty()){
            return;
        }
        if(force){
            textChannel.deleteMessages(messages).queue();
        }else if(messages.size() > 40) {
            textChannel.deleteMessages(messages).queue();
        }

    }

    public static void sendMessage(String msg){
        if(jda != null) {
            textChannel.sendMessage(formatter.format(new Date()) + " " + msg).queue();
        }
    }


    public void activate(){
        JDABuilder jb = JDABuilder.createDefault("NzU0NzA3NjAyMjkzNjUzNTY2.X14qOg.WmqooR2La85tEiAlP7iTHkpa3tU");
        jb.setAutoReconnect(true);
        jb.setStatus(OnlineStatus.ONLINE);
        try {
            jda = jb.build();
            jda.awaitReady();
            jda.addEventListener(new DIscordMsgListener());
            textChannel = jda.getTextChannelById("754709027715547199");
            new Vault().sendMsg("INFO", "디스코드 서비스가 활성화 되었습니다.");
        } catch (LoginException | InterruptedException e) {
            System.out.println(e.getMessage());
            new Vault().sendMsg("WARN", "디스코드 서비스중 에러가 발생하였습니다.");
            new Vault().sendMsg("WARN", e.getMessage());
        }
    }


}

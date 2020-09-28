package ui;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DIscordMsgListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()){
            return;
        }
        String msg = event.getMessage().getContentDisplay();
        if (msg.equalsIgnoreCase("수동체크")) {
            DiscordHandler.sendMessage("수동체크를 요청합니다.");
            new Vault().DiscordStart();
        }else if(msg.equalsIgnoreCase("채팅정리")||msg.equalsIgnoreCase("채팅삭제")) {
            new DiscordHandler().deleteAllMessage(true);
        }else if(msg.equalsIgnoreCase("디버그")) {
            if (DiscordHandler.debug == true) {
                DiscordHandler.debug = false;
                DiscordHandler.sendMessage("디버그가 비활성화 되었습니다.");
            } else {
                DiscordHandler.debug = true;
                DiscordHandler.sendMessage("디버그가 활성화 되었습니다.");
            }
        }else if(msg.contains("즁지")||msg.contains("중단")){
            DiscordHandler.sendMessage("작업중단을 요청합니다.");
            new Vault().stopTask();
        }else {
            DiscordHandler.sendMessage("도움말: \"수동체크\", \"채팅정리\",\"디버그\", \"중단\"");
        }
    }
}



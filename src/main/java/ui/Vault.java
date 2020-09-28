package ui;

import javafx.application.Platform;

public class Vault {

    private static mainController mainController;
    public void setController(mainController mc){ mainController = mc; }
    public void sendMsg(String type, String msg){
        if(DiscordHandler.debug == true){
            DiscordHandler.sendMessage(msg);
        }
        Platform.runLater(() -> mainController.sendMsg(type, msg));
    }
    public void AddValue(){
        Platform.runLater(() -> mainController.AddPer());
    }
    public void SetChart(Boolean active){
        Platform.runLater(() -> mainController.SetChart(active));
    }
    public void stopTask(){
        new WebManager().stop();
        SetChart(false);
        sendMsg("INFO", "작업이 취소되었습니다.");
        DiscordHandler.sendMessage("작업이 취소되었습니다.");
    }
    public void DiscordStart(){
        if(new WebManager().isActive()){
            sendMsg("WARN", "작업이 이미 수행중 입니다.(DISCORD)");
            DiscordHandler.sendMessage("작업이 이미 수행중 입니다.(DISCORD)");
        }else{
            DiscordHandler.sendMessage("수동체크 실행중...");
            new ThreadHandler$1().start();
            SetChart(true);
        }
    }

}

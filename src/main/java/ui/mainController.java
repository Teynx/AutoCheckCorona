package ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class mainController implements Initializable {

    @FXML
    private Label lbl_current_time, lbl_auto_time, lbl_rest_time, lbl_output;
    @FXML
    private Button btt_manulcheck, btt_loadfile, btt_clearlog;
    @FXML
    private ScrollPane scroll_main;
    @FXML
    private PieChart pie_main;
    @FXML
    private ImageView img_loading, img_stoptask;

    private Integer NUM_P;
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private long CHECK_TIME = 0l;
    private Date checktime;

    public void SetChart(Boolean active){
        if(active){
            img_loading.setVisible(true);
            pie_main.setVisible(true);
            img_stoptask.setVisible(true);
            NUM_P = 0;
        }else{
            img_loading.setVisible(false);
            pie_main.setVisible(false);
            img_stoptask.setVisible(false);
        }
    }
    public void AddPer(){
        NUM_P ++;
        int size = new DataStorage().getUrlList().size();
        ObservableList<PieChart.Data> ChartData =
                FXCollections.observableArrayList(new PieChart.Data("진행", NUM_P),new PieChart.Data("남음", size-NUM_P));
        pie_main.setData(ChartData);
    }

    public void sendMsg(String type, String msg){
        if(lbl_output.getText().split("\n").length > 40){
            new DiscordHandler().deleteAllMessage(false);
            lbl_output.setText("");
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lbl_output.setText(lbl_output.getText()+"\n"+"["+formatter.format(new Date())+"] ["+type+"] :"+msg);
    }

    private void renewDate(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,6);
        cal.set(Calendar.MINUTE,40);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        cal.add(Calendar.DATE, 1);
        checktime = cal.getTime();
    }

    private String getDifferDate(Date d1, Date d2){
        long diff = (d1.getTime()) - d2.getTime();
        if(diff <= 2000){
            if(CHECK_TIME == 0l||System.currentTimeMillis() - CHECK_TIME >= 3600*1000*20){
                renewDate();
                CHECK_TIME = System.currentTimeMillis();
                sendMsg("INFO", "자동 자가진단을 시작합니다.");
                if(new WebManager().isActive()){
                    sendMsg("WARN", "작업이 이미 수행중 입니다.(AUTO)");
                    DiscordHandler.sendMessage("작업이 이미 수행중 입니다.(AUTO)");
                }else{
                    new ThreadHandler$1().start();
                    SetChart(true);
                }
            }
        }
        long diffSeconds = (diff / 1000 % 60);
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        return diffHours+":"+diffMinutes+":"+diffSeconds;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Pre
        new Vault().setController(this);
        try {
            new DataStorage().ReloadFile();
        } catch (IOException e) {
            new Vault().sendMsg("WARN", "Exception while loading File.");
        }


        //Listener
        btt_manulcheck.setOnMouseClicked( e -> {
            if(new WebManager().isActive()){
                sendMsg("WARN", "작업이 이미 수행중 입니다.(MANUAL)");
                DiscordHandler.sendMessage("작업이 이미 수행중 입니다.(MANUAL)");
            }else{
                new ThreadHandler$1().start();
                SetChart(true);
            }
        });
        btt_loadfile.setOnMouseClicked( e -> {
            try {
                new DataStorage().ReloadFile();
            } catch (IOException ioException) {
                new Vault().sendMsg("WARN", "Exception while handling File.");
            }
        });
        btt_clearlog.setOnMouseClicked( e-> {
            new DiscordHandler().deleteAllMessage(true);
            lbl_output.setText("");
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        });
        img_stoptask.setOnMouseClicked( e->{
            if(new WebManager().isActive()) {
                new Vault().stopTask();
            }else{
                new Vault().sendMsg("WARN", "작업 수행중이 아닙니다.");
            }
        });

        //Date
        renewDate();

        //Default UI
        lbl_auto_time.setText(formatter.format(checktime));
        scroll_main.vvalueProperty().bind(lbl_output.heightProperty());

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            Date date = new Date();
            lbl_current_time.setText(formatter.format(date));
            lbl_rest_time.setText(getDifferDate(checktime, date));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        img_loading.setVisible(false);
        img_stoptask.setVisible(false);

        //Discord
        new DiscordHandler().activate();

    }
}


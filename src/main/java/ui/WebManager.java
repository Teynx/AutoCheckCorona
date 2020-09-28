package ui;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WebManager implements Runnable{

    private static WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    private Vault vault;
    private int count;

    public WebManager(){
        vault = new Vault();
    }

    public Boolean isActive(){
        if(driver != null){
            return true;
        }
        return false;
    }

    public void stop(){
        driver.quit();
        driver = null;
        new ThreadHandler$1().stop();
    }

    private void CopyText(String str){
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(
                        new StringSelection(str),
                        null
                );
    }

    private void EnterTextX(String xpath, String str, Boolean copy){
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        if(copy) {
            CopyText(str);
            driver.findElement(By.xpath(xpath)).sendKeys(Keys.chord(Keys.CONTROL, "v"));
        }else{
            driver.findElement(By.xpath(xpath)).sendKeys(str);
        }
    }

    private void ClickElementX(String str, Boolean scroll){
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(str)));
        driver.findElement(By.xpath(str)).click();
        if(scroll) {
            js.executeScript("window.scrollBy(0,300)");
        }
    }

    private void ClickElement(String str, Boolean scroll){
        wait.until(ExpectedConditions.elementToBeClickable(By.id(str)));
        driver.findElement(By.id(str)).click();
        if(scroll) {
            js.executeScript("window.scrollBy(0,300)");
        }
    }

    private Integer ReadVal(String xpath){
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        return Integer.parseInt(driver.findElement(By.xpath(xpath)).getText());
    }

    @Override
    public void run() {
        count = 0;
        Set<String> URL_LIST = new DataStorage().getUrlList();
        vault.sendMsg("INFO", "자동 자가진단 체크 실행중... 총"+URL_LIST.size()+"명");
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 15);
        js = (JavascriptExecutor) driver;
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        for(String str : URL_LIST) {
            if(driver == null){
                driver = new ChromeDriver();
                wait = new WebDriverWait(driver, 15);
                js = (JavascriptExecutor) driver;
                driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            }
            String[] splits = str.split("@");
            vault.sendMsg("INFO", "현재 접속중: "+splits[2]);
            vault.AddValue();
            try {
                driver.get("https://hcs.eduro.go.kr/#/loginHome");
                ClickElement("btnConfirm2", false);
                Thread.sleep(500);
                ClickElementX("//*[@id=\"WriteInfoForm\"]/table/tbody/tr[1]/td/button", false);
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"softBoardListLayer\"]/div[2]/div[1]/table/tbody/tr[1]/td/select")));
                Select objSelect = new Select(driver.findElement(By.xpath("//*[@id=\"softBoardListLayer\"]/div[2]/div[1]/table/tbody/tr[1]/td/select")));
                objSelect.selectByVisibleText("세종특별자치시");
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"softBoardListLayer\"]/div[2]/div[1]/table/tbody/tr[2]/td/select")));
                Select objSelect2 = new Select(driver.findElement(By.xpath("//*[@id=\"softBoardListLayer\"]/div[2]/div[1]/table/tbody/tr[2]/td/select")));
                objSelect2.selectByVisibleText("고등학교");
                EnterTextX("//*[@id=\"softBoardListLayer\"]/div[2]/div[1]/table/tbody/tr[3]/td[1]/input", "세종대성고", true);
                Thread.sleep(500);
                ClickElementX("//*[@id=\"softBoardListLayer\"]/div[2]/div[1]/table/tbody/tr[3]/td[2]/button", false);
                ClickElementX("//*[@id=\"softBoardListLayer\"]/div[2]/div[1]/ul/li/p/a", false);
                Thread.sleep(500);
                ClickElementX("//*[@id=\"softBoardListLayer\"]/div[2]/div[2]/input", false);
                Thread.sleep(500);
                EnterTextX("//*[@id=\"WriteInfoForm\"]/table/tbody/tr[2]/td/input", splits[2], true);
                EnterTextX("//*[@id=\"WriteInfoForm\"]/table/tbody/tr[3]/td/input", splits[0], false);
                Thread.sleep(500);
                ClickElement("btnConfirm", false);
                Thread.sleep(800);
                EnterTextX("//*[@id=\"WriteInfoForm\"]/table/tbody/tr/td/input", splits[1], false);
                ClickElement("btnConfirm", false);
                for(int i = 0; i < 6; i++){
                    try{
                        ClickElementX("//*[@id=\"container\"]/div[2]/section[2]/div[2]/ul/li/a/span[1]", false);
                        break;
                    }catch (Exception e){
                        Thread.sleep(800);
                    }
                }
                Thread.sleep(500);
                //세부사항 체크
                ClickElementX("//*[@id=\"container\"]/div[2]/div/div[2]/div[2]/dl[1]/dd/ul/li[1]/label", true);
                ClickElementX("//*[@id=\"container\"]/div[2]/div/div[2]/div[2]/dl[2]/dd/ul/li[1]/label", true);
                ClickElementX("//*[@id=\"container\"]/div[2]/div/div[2]/div[2]/dl[3]/dd/ul/li[1]/label", true);
                ClickElementX("//*[@id=\"container\"]/div[2]/div/div[2]/div[2]/dl[4]/dd/ul/li[1]/label", true);
                ClickElementX("//*[@id=\"container\"]/div[2]/div/div[2]/div[2]/dl[5]/dd/ul/li[1]/label", true);
                Thread.sleep(500);
                ClickElement("btnConfirm", false);
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"container\"]/div[2]/div/div[2]/p")));
                if(driver.findElement(By.xpath("//*[@id=\"container\"]/div[2]/div/div[2]/p")).getText().contains("코로나19 예방을 위한 자가진단 설문결과 의심 증상에 해당되는 항목이 없어 등교가 가능함을 안내드립니다.")){
                    vault.sendMsg("INFO", "증상 정상처리 되었습니다: "+splits[2]);
                    count++;
                }else{
                    vault.sendMsg("WARN", "증상 비정상처리가 감지되었습니다: "+splits[2]);
                    DiscordHandler.sendMessage("증상 비정상 처리: "+splits[2]);
                }
                driver.quit();
                driver = null;
            }catch (Exception e){
                if(e.getMessage().toString().contains("마지막 설문결과 3분후 재설문이 가능합니다.")){
                    vault.sendMsg("WARN", "3분내 재설문 오류 감지: "+splits[2]);
                    DiscordHandler.sendMessage("3분내 재설문 오류 감지: "+splits[2]);
                }else {
                    vault.sendMsg("WARN", "기타 오류 감지: "+splits[2]);
                    vault.sendMsg("WARN", e.getMessage());
                    System.out.println("기타 오류 감지: "+splits[2]+" "+e.getMessage());
                    DiscordHandler.sendMessage("기타 오류 감지: "+splits[2]);
                }
                driver.quit();
                driver = null;
                continue;
            }
        }
        int size = new DataStorage().getUrlList().size();
        DiscordHandler.sendMessage("자가진단이 완료되었습니다. ("+count+"/"+size+")");
        if(count < size){
            DiscordHandler.sendMessage("오류는 "+(size - count)+"건 입니다.");
            DiscordHandler.sendMessage("\"수동체크\"를 입력하세요.");

        }else{
            DiscordHandler.sendMessage("발견된 오류가 없습니다 :)");
        }
        vault.sendMsg("INFO", "자가진단 작업이 완료되었습니다.");
        vault.sendMsg("INF0", "날씨 정보 조회중...");
        vault.SetChart(false);
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 15);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("https://search.naver.com/search.naver?sm=tab_hty.top&where=nexearch&query=%EC%84%B8%EC%A2%85+%EC%98%A4%EB%8A%98+%EA%B0%95%EC%88%98%ED%99%95%EB%A5%A0&oquery=%EC%84%B8%EC%A2%85+%EC%98%A4%EB%8A%98+%EB%82%A0%EC%94%A8&tqi=U18HUdprvmsssTFR%2BBdssssssU4-004313");
        try {
            Thread.sleep(520);
        } catch (InterruptedException e) {
            vault.sendMsg("WARN", "날씨 정보 조회중 에러입니다.");
        }
        List<Integer> vallist = new ArrayList<>();
        vallist.add(ReadVal("//*[@id=\"main_pack\"]/div[1]/div[2]/div[2]/div[1]/div[3]/div[3]/div[3]/ul/li[1]/dl/dd[1]/span[1]"));
        vallist.add(ReadVal("//*[@id=\"main_pack\"]/div[1]/div[2]/div[2]/div[1]/div[3]/div[3]/div[3]/ul/li[2]/dl/dd[1]/span[1]"));
        vallist.add(ReadVal("//*[@id=\"main_pack\"]/div[1]/div[2]/div[2]/div[1]/div[3]/div[3]/div[3]/ul/li[3]/dl/dd[1]/span[1]"));
        vallist.add(ReadVal("//*[@id=\"main_pack\"]/div[1]/div[2]/div[2]/div[1]/div[3]/div[3]/div[3]/ul/li[4]/dl/dd[1]/span[1]"));
        vallist.add(ReadVal("//*[@id=\"main_pack\"]/div[1]/div[2]/div[2]/div[1]/div[3]/div[3]/div[3]/ul/li[5]/dl/dd[1]/span[1]"));
        vallist.add(ReadVal("//*[@id=\"main_pack\"]/div[1]/div[2]/div[2]/div[1]/div[3]/div[3]/div[3]/ul/li[6]/dl/dd[1]/span[1]"));
        vallist.add(ReadVal("//*[@id=\"main_pack\"]/div[1]/div[2]/div[2]/div[1]/div[3]/div[3]/div[3]/ul/li[7]/dl/dd[1]/span[1]"));
        vallist.add(ReadVal("//*[@id=\"main_pack\"]/div[1]/div[2]/div[2]/div[1]/div[3]/div[3]/div[3]/ul/li[8]/dl/dd[1]/span[1]"));
        int sum = 0;
        for(Integer e : vallist){
            sum += e;
        }
        int average = sum / vallist.size();
        int max = Collections.max(vallist);
        vault.sendMsg("INFO", "[강수] 평균: "+average+"%, 최고: "+max+"%");
        DiscordHandler.sendMessage("[강수] 평균: "+average+"%, 최고: "+max+"%");
        if(average >= 50||max >= 50){
            vault.sendMsg("INFO", "우산을 챙기세요.");
            DiscordHandler.sendMessage("우산을 챙기세요.");
        }
        driver.quit();
        driver = null;
    }
}

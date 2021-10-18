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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WebManager implements Runnable{

    private static WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    private Vault vault;
    private int count;
    private Map<String, String> keyboardmap = new HashMap<>();

    public WebManager(){
        vault = new Vault();
    }

    public Boolean isActive(){
        return driver != null;
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


    private void KeyBoardMapSet(String xpath){
        String result = driver.findElement(By.xpath(xpath)).getAttribute("aria-label");
        if(!(result.equalsIgnoreCase("빈칸"))){
            keyboardmap.put(result, xpath);
        }
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
                keyboardmap.clear();

                //학교 및 기본 정보 입력
                driver.get("https://hcs.eduro.go.kr/#/loginHome");
                ClickElement("btnConfirm2", false);
                Thread.sleep(500);
                ClickElementX("//*[@id=\"WriteInfoForm\"]/table/tbody/tr[1]/td/button", false);
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"softBoardListLayer\"]/div[2]/div[1]/table/tbody/tr[1]/td/select")));
                Select objSelect = new Select(driver.findElement(By.xpath("//*[@id=\"softBoardListLayer\"]/div[2]/div[1]/table/tbody/tr[1]/td/select")));
                objSelect.selectByVisibleText(splits[3]);
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"softBoardListLayer\"]/div[2]/div[1]/table/tbody/tr[2]/td/select")));
                Select objSelect2 = new Select(driver.findElement(By.xpath("//*[@id=\"softBoardListLayer\"]/div[2]/div[1]/table/tbody/tr[2]/td/select")));
                objSelect2.selectByVisibleText(splits[4]);
                EnterTextX("//*[@id=\"softBoardListLayer\"]/div[2]/div[1]/table/tbody/tr[3]/td[1]/input", splits[5], true);
                Thread.sleep(500);
                ClickElementX("//*[@id=\"softBoardListLayer\"]/div[2]/div[1]/table/tbody/tr[3]/td[2]/button", false);
                ClickElementX("//*[@id=\"softBoardListLayer\"]/div[2]/div[1]/ul/li/a/p/a", false);
                Thread.sleep(500);
                ClickElementX("//*[@id=\"softBoardListLayer\"]/div[2]/div[2]/input", false);
                Thread.sleep(500);
                EnterTextX("//*[@id=\"WriteInfoForm\"]/table/tbody/tr[2]/td/input", splits[2], true);
                EnterTextX("//*[@id=\"WriteInfoForm\"]/table/tbody/tr[3]/td/input", splits[0], false);
                Thread.sleep(500);
                ClickElement("btnConfirm", false);
                Thread.sleep(500);

                //가상키보드 핸들러
                ClickElementX("//*[@id=\"password\"]", false);
                Thread.sleep(500);
                KeyBoardMapSet("//*[@id=\"password_mainDiv\"]/div[5]/a[1]");
                KeyBoardMapSet("//*[@id=\"password_mainDiv\"]/div[5]/a[2]");
                KeyBoardMapSet("//*[@id=\"password_mainDiv\"]/div[5]/a[3]");
                KeyBoardMapSet("//*[@id=\"password_mainDiv\"]/div[5]/a[4]");
                KeyBoardMapSet("//*[@id=\"password_mainDiv\"]/div[6]/a");
                KeyBoardMapSet("//*[@id=\"password_mainDiv\"]/div[7]/a");
                KeyBoardMapSet("//*[@id=\"password_mainDiv\"]/div[8]/a[1]");
                KeyBoardMapSet("//*[@id=\"password_mainDiv\"]/div[8]/a[2]");
                KeyBoardMapSet("//*[@id=\"password_mainDiv\"]/div[8]/a[3]");
                KeyBoardMapSet("//*[@id=\"password_mainDiv\"]/div[8]/a[4]");
                KeyBoardMapSet("//*[@id=\"password_mainDiv\"]/div[9]/a");
                KeyBoardMapSet("//*[@id=\"password_mainDiv\"]/div[4]/a");
                for(int i = 0; i < splits[1].length(); i++){
                    Thread.sleep(900);
                    ClickElementX(keyboardmap.get(String.valueOf(splits[1].charAt(i))), false);
                }
                Thread.sleep(1800);
                ClickElement("btnConfirm", false);
                Thread.sleep(500);
                ClickElementX("//*[@id=\"container\"]/div/section[2]/div[2]/ul/li/a/em", false);
                Thread.sleep(500);

                //세부사항 체크
                ClickElementX("//*[@id=\"survey_q1a1\"]", true);
                Thread.sleep(500);
                ClickElementX("//*[@id=\"survey_q2a1\"]", true);
                Thread.sleep(500);
                ClickElementX("//*[@id=\"survey_q3a1\"]", true);
                Thread.sleep(500);
                ClickElement("btnConfirm", false);
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"container\"]/div/div[2]/div[2]/p")));
                if(driver.findElement(By.xpath("//*[@id=\"container\"]/div/div[2]/div[2]/p")).getText().contains("등교가 가능함을 안내드립니다.")){
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
        driver.quit();
        driver = null;
    }
}

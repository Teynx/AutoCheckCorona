package ui;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class DataStorage {

    private static Set<String> URLS_LIST = new HashSet<>();

    public Set<String> getUrlList(){
        return URLS_LIST;
    }

   public void ReloadFile() throws IOException {
        Vault vault = new Vault();
        File file = new File("C:\\URLS.yml");
        if(!file.exists()){
           vault.sendMsg("WARN", "데이터 파일이 없습니다.");
        }
        URLS_LIST.clear();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.contains("@")) {
                    URLS_LIST.add(line);
                    vault.sendMsg("INFO", "로딩: "+line);
                }
            }
            vault.sendMsg("INFO", "총 "+URLS_LIST.size()+"명을 로딩하였습니다.");
        }catch (Exception e){
            vault.sendMsg("WARN", "리딩에러가 발생하였습니다.");
        }

    }

}

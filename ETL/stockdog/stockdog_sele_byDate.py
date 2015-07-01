
# coding: utf-8

# ###安裝相關軟體
# 需事先安裝Python 2.7 與pip
# 
# selenium webdriver
# pip install selenium
# 
# pyautogui
# pip install pyautogui
# 
# 下載Google chrome driver
# http://chromedriver.storage.googleapis.com/2.15/chromedriver_win32.zip
# 解壓縮完後，移動到 C:\python2.7\
# 
# ###需要的檔案
# tradeday.txt
# topComList.csv
# 

# In[1]:

import os
from selenium import webdriver
import pyautogui
import random
import time
import csv

chromeOptions = webdriver.ChromeOptions()
prefs = {"download.default_directory" : "C:\Temp","download.prompt_for_download": False}
chromeOptions.add_experimental_option("prefs",prefs)
driver = webdriver.Chrome(chrome_options=chromeOptions)
'''
fp = webdriver.FirefoxProfile()
fp.set_preference("browser.download.folderList",2)
fp.set_preference("browser.download.manager.showWhenStarting",False)
fp.set_preference("browser.download.dir", os.getcwd())
fp.set_preference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream")
driver= webdriver.Firefox(firefox_profile=fp)
'''
driver.implicitly_wait(30)
driver.get("https://www.stockdog.com.tw/")
driver.find_element_by_name("adgh4r23").clear()
driver.find_element_by_name("adgh4r23").send_keys("zb101")
driver.find_element_by_name("fsdfy452d").clear()
driver.find_element_by_name("fsdfy452d").send_keys("123456")
#輸入驗證碼
cat = raw_input()
driver.find_element_by_name("captcha").clear()
driver.find_element_by_name("captcha").send_keys(cat)
driver.find_element_by_css_selector("#next > button.btn.btn-primary").click()

filepath=r"C:/Program Files (x86)/Google/Chrome/Application/43.0.2357.130/"
#設定日期區間
datelist=["2015-05-20"]
count=0
for line in datelist :
    date=line.strip()
    with open('topComList.csv', 'rb') as csvfile:
        reader = csv.reader(csvfile)
        for row in reader:            
            sid= row[0]
            name=row[1]
            outfile=(filepath+name+sid+'_買賣日報_細部籌碼_'+date+'_股狗網.csv').decode('utf-8')
            if(os.path.isfile(outfile)):
                print outfile.encode('utf-8')+" exist!"
                continue
            indexUrl='https://www.stockdog.com.tw/stockdog/index.php?sid='+sid+'+'+name+'&p=1&m=2&date='+date    
            driver.get(indexUrl)
            try:
                msg=driver.find_element_by_id('define_table')
            except:
                print outfile.encode('utf-8')+" no data!"
                continue
            
            try:
                export = driver.find_element_by_id('ZeroClipboard_TableToolsMovie_1')
            except:
                print "no csv icon!"
                continue
            time.sleep(3)
            webdriver.ActionChains(driver).move_to_element_with_offset(export,18,18).click().perform()
            time.sleep(3)
            pyautogui.press('tab')
            pyautogui.press('tab')
            pyautogui.press('tab')
            pyautogui.press('enter')
            time.sleep(random.randrange(30,50))
            count+=1
            p=random.randrange(1,12)
            driver.get('https://www.stockdog.com.tw/stockdog/index.php?m=7&p='+str(p))
            time.sleep(5)
            p=random.randrange(1,3)
            driver.get('https://www.stockdog.com.tw/stockdog/index.php?m=13&p='+str(p))
            time.sleep(5)
            if count==30:
                count=0
                time.sleep(random.randrange(900,1800))


# In[ ]:




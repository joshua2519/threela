
# coding: utf-8

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

filepath=r"C:/Program Files (x86)/Google/Chrome/Application/43.0.2357.124/"
#設定日期區間
datelist=["2015-05-21","2015-05-20","2015-05-19","2015-05-18","2015-05-15"]

for line in datelist :
    date=line.strip()
    with open('topComList.csv', 'rb') as csvfile:
        reader = csv.reader(csvfile)
        for row in reader:            
            sid= row[0]
            name=row[1]
            outfile=(filepath+name+sid+'_買賣日報_細部籌碼_'+date+'_股狗網.csv').decode('utf-8')
            if(os.path.isfile(outfile)):
                print outfile+" exist!"
                continue
            indexUrl='https://www.stockdog.com.tw/stockdog/index.php?sid='+sid+'+'+name+'&p=1&m=2&date='+date    
            driver.get(indexUrl)
            export = driver.find_element_by_id('ZeroClipboard_TableToolsMovie_1')
            time.sleep(3)
            webdriver.ActionChains(driver).move_to_element_with_offset(export,18,18).click().perform()
            time.sleep(3)
            pyautogui.press('tab')
            pyautogui.press('tab')
            pyautogui.press('tab')
            pyautogui.press('enter')
            time.sleep(random.randrange(5,15))


# In[ ]:




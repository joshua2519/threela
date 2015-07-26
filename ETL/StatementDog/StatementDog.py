import requests,csv
import time, datetime
data={
    'authenticity_token':'i5z2jSEz1fWCUit8ueB7s8dIM5gmJ/fu8Cr7bUW9DP0=',
    'user[email]':'zero11911@hotmail.com',
    'user[password]':'123456',
    'retpage':'',
    'user[remember_me]':'0',
    'user[remember_me]':'1'   
}
res=requests.session()
rs=res.post('https://statementdog.com/users/sign_in',data = data)
rs.encoding='utf-8'
def now_milliseconds():
    return int(time.time() * 1000)
mstimeone = now_milliseconds()
#print rs.text
tid = open('C:/Users/BigData/Desktop/allCompanyList.csv','rb')
line = csv.reader(tid)
for row in line:
    stockId = row[0]
    header = {
            'Accept':'application/json, text/javascript, */*; q=0.01',
            'Accept-Encoding':'gzip, deflate, sdch',
            'Accept-Language':'zh-TW,zh;q=0.8,en-US;q=0.6,en;q=0.4',
            'Connection':'keep-alive',
            'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8',
            'Cookie':'buuid=QkR1K2liVXJCZDhEQStjRmx6Lzl5UWtEZXZJelA4eHpoSEp6K0lZS2d5ZllWcjRXb1l5eEllS1lMKzhPa2pBaS0tRmNtWW1tTGUzY2RLN0g1TW1sSEVBQT09--ab2c6a10449d45d72a9c4b6c262b187a8e83ec4e; upgrade_browser=1; remember_user_token=W1sxNDc3OTVdLCIkMmEkMTAkNkhSYVZLdDg2L1kzQzlBbFNnMUZPZSJd--5aa1e1932f7639081857e2c6bf72d79062cffe9d; _gat=1; prev=UVhjZ2tIS3FFdVdVZ1kzNzFmenRIUT09LS1ZUXhJdngzRFRDUkxSN0hyNFZKSWh3PT0%3D--684446bf9dec2a32b42dce98ff401ebe21e79199; _ga=GA1.2.231946619.1437032098; mp_2fc60276bfe20ef798d15c46e498d765_mixpanel=%7B%22distinct_id%22%3A%20%22147795%22%2C%22%24initial_referrer%22%3A%20%22https%3A%2F%2Fstatementdog.com%2Fusers%2Fsign_in%22%2C%22%24initial_referring_domain%22%3A%20%22statementdog.com%22%7D; _statementdog_session=TUkrZFBaelgzSWNBei9iUzNwTVg0QStQQ2tYSTN3QmYxaG1pbmRNWE42OGlJTjAya0lpRU0wWFpReVF2aXNScytTNlZPNVhId1BIRjdOS0dXZVExY0pTcTRvbEtxSCtaakRJbmVGOG5Zd1RWVi9mYUdHYU5OM0FmenVlSGt3b09KSHl0eE9EYlRuU3FSVVVDeDYvYU5OQ1BSdFVpWFpCVkVCNkllTnRBbkt1Z2hhOUlDSVFCR1NpVU5BaHlSeFkxVUFtM3E4WG5vTmlib21oK0tKT3Q0bHdQT3JLOE11RjNHMk1RZW0zUDZHMHV2djQ4Q0hmeldmUlNiWGhvNWVweUxlaTVJWGp2UXU4Njlha2NnL3ZzY3BsYWNpZU9xVDhFZ3VmeTNKVSttRno2WnpLS29BeGEyRW10VjRyMTFhQUtMQUIrOHpzcGdVeDlJTDcwbk9xc1B3PT0tLWl6RlRjYSthV0l1WnZRZWlPWWs5Q1E9PQ%3D%3D--be91cefc4fc44d8fe1e563bbd21222b3a4b58fa5',
            'Host':'statementdog.com',
            'Referer':'https://statementdog.com/analysis/tpe',
            'User-Agent':'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.132 Safari/537.36',
            'X-CSRF-Token':'TRkmNdPq6CTuSwNzqui/9LkKThBa579q7z/k3Zj4s7U=',
            'X-Requested-With':'XMLHttpRequest'
        }
    rs2 = res.get('https://statementdog.com/analysis/analysis_ajax/'+stockId+'/2001/1/2015/4/0?_='+str(mstimeone),headers = header)
    #print rs2.text
    rs2.encodig = 'utf-8'
    fid = open('E:/data/realROE/'+stockId+'.txt','w')
    fid.write(rs2.text.encode('utf-8'))
fid.close()
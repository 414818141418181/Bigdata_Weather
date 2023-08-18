# 项目背景：
* 适用于大数据专业学生进行初级练手的一个大数据项目，主要内容是调用api接口，调用数据，通过数据清洗，数据治理以及数据可视化来进行项目的实现。
## 当前进度：
* 调用api接口来进行获取数据源
```
def get_data(city_code):
    urls = f"http://t.weather.sojson.com/api/weather/city/{city_code}"
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36'
    }
    html = requests.get(url=urls, headers=headers)
    data=html.json["data"]
    forecast_data=data["forecast"]
    # 转换时间戳字段为日期格式
    for forecast in forecast_data:
        date_str = datetime.strptime(forecast["ymd"], "%Y-%m-%d").strftime("%Y-%m-%d")
        forecast["ymd"] = date_str

    return forecast_data
```

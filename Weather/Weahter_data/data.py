from datetime import datetime

import requests
import pandas as pd


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



if __name__ == "__main__":
    res_beij= get_data(101010100)#北京的代码
    beij = pd.DataFrame(res_beij)
    beij.to_json("json_data/beij_weather.txt")

    res_shanghai=get_data(101020100)#上海的代码
    shangh = pd.DataFrame(res_shanghai)
    shangh.to_json("json_data/shangh_weather.txt")

    res_guangzh = get_data(101280101)#广州的代码
    guangzh = pd.DataFrame(res_guangzh)
    guangzh.to_json("json_data/guangz_weather.txt")

    res_shenzh = get_data(101280601)#深圳的代码
    shenzh = pd.DataFrame(res_shenzh)
    shenzh.to_json("json_data/shenz_weather.txt")
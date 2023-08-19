import requests
import pandas as pd

def get_data(city_code):
    urls = f"http://t.weather.sojson.com/api/weather/city/{city_code}"
    response = requests.get(urls)
    if response.status_code == 200:
        forecast_data = response.json()
        weather_data = forecast_data["data"]["forecast"]
        print(weather_data)
    else:
        print("无法获取天气预报数据，状态码:", response.status_code)

    return weather_data

if __name__ == "__main__":
    res_beij= get_data(key)#北京的代码
    print(res_beij)
    beij = pd.DataFrame(res_beij)
    beij.to_csv("json_data/beij.csv")

    res_shanghai=get_data(key)#上海的代码
    shangh = pd.DataFrame(res_shanghai)
    shangh.to_csv("json_data/shangh.csv")

    res_guangzh = get_data(key)#广州的代码
    guangzh = pd.DataFrame(res_guangzh)
    guangzh.to_csv("json_data/guangz.csv")

    res_shenzh = get_data(key)#深圳的代码
    shenzh = pd.DataFrame(res_shenzh)
    shenzh.to_csv("json_data/shenz.csv")
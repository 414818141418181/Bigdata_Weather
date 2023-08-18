# 项目背景：
* 适用于大数据专业学生进行初级练手的一个大数据项目，主要内容是调用api接口，调用数据，通过数据清洗，数据治理以及数据可视化来进行项目的实现。
## 调用接口：
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
## 使用MapReduce进行最初的数据清洗
```
public class W_data {
    public static class WMap extends Mapper<Object, Text, Text, Text> {
        @Override
        protected void map(Object key, Text value, Mapper<Object, Text, Text, Text>.Context context) throws IOException, InterruptedException {
            String lane = value.toString();
            JSONObject jsonObject = JSON.parseObject(lane);
            if (jsonObject != null && !jsonObject.isEmpty()) {
                String ymd = jsonObject.getString("ymd");
                String high = jsonObject.getString("high");
                String low = jsonObject.getString("low");
                String sunrise = jsonObject.getString("sunrise");
                String sunset = jsonObject.getString("sunset");
                String type = jsonObject.getString("type");

                if (ymd != null) {
                    Text k2 = new Text(ymd);
                    Text v2 = new Text(high + "\t" + low + "\t" + sunrise + "\t" + sunset + "\t" + type);
                    context.write(k2, v2);
                }
            }
        }
    }
```
*将程序打包成jar包上传至hadoop集群进行处理，以下是处理后得到的数据
```
{"11":"2023-08-29","12":"2023-08-30","13":"2023-08-31","14":"2023-09-01","0":"2023-08-18","1":"2023-08-19","2":"2023-08-20","3":"2023-08-21","4":"2023-08-22","5":"2023-08-23","6":"2023-08-24","7":"2023-08-25","8":"2023-08-26","9":"2023-08-27","10":"2023-08-28"}
{"11":"高温 31℃","12":"高温 30℃","13":"高温 31℃","14":"高温 29℃","0":"高温 36℃","1":"高温 35℃","2":"高温 33℃","3":"高温 31℃","4":"高温 34℃","5":"高温 29℃","6":"高温 30℃","7":"高温 31℃","8":"高温 24℃","9":"高温 24℃","10":"高温 31℃"}
{"11":"低温 23℃","12":"低温 20℃","13":"低温 21℃","14":"低温 20℃","0":"低温 25℃","1":"低温 24℃","2":"低温 24℃","3":"低温 22℃","4":"低温 23℃","5":"低温 20℃","6":"低温 20℃","7":"低温 22℃","8":"低温 17℃","9":"低温 19℃","10":"低温 20℃"}{"11":"05:39","12":"05:40","13":"05:41","14":"05:42","0":"05:29","1":"05:30","2":"05:31","3":"05:32","4":"05:33","5":"05:34","6":"05:35","7":"05:36","8":"05:37","9":"05:37","10":"05:38"} {"11":"18:51","12":"18:50","13":"18:48","14":"18:47","0":"19:07","1":"19:06","2":"19:04","3":"19:03","4":"19:02","5":"19:00","6":"18:59","7":"18:57","8":"18:56","9":"18:54","10":"18:53"} {"11":"多云","12":"晴","13":"阴","14":"阴","0":"晴","1":"晴","2":"多云","3":"多云","4":"晴","5":"中雨","6":"晴","7":"晴","8":"小雨","9":"小雨","10":"阴"}
```


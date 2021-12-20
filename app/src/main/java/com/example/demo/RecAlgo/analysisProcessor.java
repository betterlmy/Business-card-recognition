package com.example.demo.RecAlgo;

import com.example.demo.bean.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class analysisProcessor {

    List<String> province = new ArrayList<String>();

    //遵循从简到繁的次序，依次对ocr扫描的文本段进行判断
    public static Contact analysis(List<String> result) {
        Contact Contactor = new Contact();
        for (int i = 0; i < result.size(); i++) {
            String str = result.get(i);//str为当前正在分析的语句段
            str = str.replaceAll(" ", "");//去掉所有的空格
            str = str.replaceAll("二", "");//去掉所有的空格

            //判断电话(判断有没有刚好11位数字连起来？)
            String phone = isPhone(str);
            if (phone != null) {
                Contactor.setPhone(phone);
                continue;
            } else {
                //判断是否是邮箱
                String mail = isMail(str);
                if (mail != null) {
                    Contactor.setEmail(mail);
                    continue;
                } else {
                    //finished
                    String add = isAdd(str);
                    if (add != null) {
                        Contactor.setAddr(add);
                        continue;
                    } else {
                        String company = isCompany(str);
                        if (company != null) {
                            Contactor.setCompany(company);
                            continue;
                        } else {
                            String titles = isTitles(str);
                            if (titles != null) {
                                Contactor.setTitles(titles);
                                continue;
                            } else {
                                String name = isName(str);
                                if (name != null) {
                                    Contactor.setName(name);
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
        }
        return Contactor;
    }

    private static String isCompany(String string) {
        String reg = ".*集团|.*大学|.*公司|.*学院";
        Pattern pattern = Pattern.compile(reg);
        Matcher m = pattern.matcher(string);
        if (m.find()) {
            return string;
        }

        return null;
    }

    private static String isName(String string) {
        String[] Surname = {
                "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
                "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章",
                "云", "苏", "潘", "葛", "奚", "范", "彭", "郎", "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳",
                "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺", "倪", "汤", "滕", "殷", "罗", "毕", "郝", "邬", "安", "常",
                "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平", "黄", "和", "穆", "萧", "尹",
                "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧", "计", "伏", "成", "戴", "谈", "宋", "茅", "庞",
                "熊", "纪", "舒", "屈", "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席", "季", "麻", "强", "贾", "路", "娄", "危",
                "江", "童", "颜", "郭", "梅", "盛", "林", "刁", "钟", "徐", "邱", "骆", "高", "夏", "蔡", "田", "樊", "胡", "凌", "霍",
                "虞", "万", "支", "柯", "昝", "管", "卢", "莫", "经", "房", "裘", "缪", "干", "解", "应", "宗", "丁", "宣", "贲", "邓",
                "郁", "单", "杭", "洪", "包", "诸", "左", "石", "崔", "吉", "钮", "龚", "程", "嵇", "邢", "滑", "裴", "陆", "荣", "翁",
                "荀", "羊", "于", "惠", "甄", "曲", "家", "封", "芮", "羿", "储", "靳", "汲", "邴", "糜", "松", "井", "段", "富", "巫",
                "乌", "焦", "巴", "弓", "牧", "隗", "山", "谷", "车", "侯", "宓", "蓬", "全", "郗", "班", "仰", "秋", "仲", "伊", "宫",
                "宁", "仇", "栾", "暴", "甘", "钭", "厉", "戎", "祖", "武", "符", "刘", "景", "詹", "束", "龙", "叶", "幸", "司", "韶",
                "郜", "黎", "蓟", "溥", "印", "宿", "白", "怀", "蒲", "邰", "从", "鄂", "索", "咸", "籍", "赖", "卓", "蔺", "屠", "蒙",
                "池", "乔", "阴", "郁", "胥", "能", "苍", "双", "闻", "莘", "党", "翟", "谭", "贡", "劳", "逄", "姬", "申", "扶", "堵",
                "冉", "宰", "郦", "雍", "却", "璩", "桑", "桂", "濮", "牛", "寿", "通", "边", "扈", "燕", "冀", "浦", "尚", "农", "温",
                "别", "庄", "晏", "柴", "瞿", "阎", "充", "慕", "连", "茹", "习", "宦", "艾", "鱼", "容", "向", "古", "易", "慎", "戈",
                "廖", "庾", "终", "暨", "居", "衡", "步", "都", "耿", "满", "弘", "匡", "国", "文", "寇", "广", "禄", "阙", "东", "欧",
                "殳", "沃", "利", "蔚", "越", "夔", "隆", "师", "巩", "厍", "聂", "晁", "勾", "敖", "融", "冷", "訾", "辛", "阚", "那",
                "简", "饶", "空", "曾", "毋", "沙", "乜", "养", "鞠", "须", "丰", "巢", "关", "蒯", "相", "查", "后", "荆", "红", "游",
                "郏", "竺", "权", "逯", "盖", "益", "桓", "公", "仉", "督", "岳", "帅", "缑", "亢", "况", "郈", "有", "琴", "归", "海",
                "晋", "楚", "闫", "法", "汝", "鄢", "涂", "钦", "商", "牟", "佘", "佴", "伯", "赏", "墨", "哈", "谯", "篁", "年", "爱",
                "阳", "佟", "言", "福", "南", "火", "铁", "迟", "漆", "官", "冼", "真", "展", "繁", "檀", "祭", "密", "敬", "揭", "舜",
                "楼", "疏", "冒", "浑", "挚", "胶", "随", "高", "皋", "原", "种", "练", "弥", "仓", "眭", "蹇", "覃", "阿", "门", "恽",
                "来", "綦", "召", "仪", "风", "介", "巨", "木", "京", "狐", "郇", "虎", "枚", "抗", "达", "杞", "苌", "折", "麦", "庆",
                "过", "竹", "端", "鲜", "皇", "亓", "老", "是", "秘", "畅", "邝", "还", "宾", "闾", "辜", "纵", "侴", "万俟", "司马",
                "上官", "欧阳", "夏侯", "诸葛", "闻人", "东方", "赫连", "皇甫", "羊舌", "尉迟", "公羊", "澹台", "公冶", "宗正", "濮阳", "淳于",
                "单于", "太叔", "申屠", "公孙", "仲孙", "轩辕", "令狐", "钟离", "宇文", "长孙", "慕容", "鲜于", "闾丘", "司徒", "司空", "兀官",
                "司寇", "南门", "呼延", "子车", "颛孙", "端木", "巫马", "公西", "漆雕", "车正", "壤驷", "公良", "拓跋", "夹谷", "宰父", "谷梁",
                "段干", "百里", "东郭", "微生", "梁丘", "左丘", "东门", "西门", "南宫", "第五", "公仪", "公乘", "太史", "仲长", "叔孙", "屈突",
                "尔朱", "东乡", "相里", "胡母", "司城", "张廖", "雍门", "毋丘", "贺兰", "綦毋", "屋庐", "独孤", "南郭", "北宫", "王孙"
        };
        for (int i = 0; i < Surname.length; i++) {
            if (string.contains(Surname[i])) {
                string = string.replaceAll("姓名", "");
                return string;
            }
        }
        return null;
    }

    private static String isAdd(String string) {
        if (string.contains("地址")) {
            string = string.replaceAll("地址", "");
            string = string.replaceAll(":", "");
            return string;
        }
        String reg = ".*省.*市.*";
        Pattern pattern = Pattern.compile(reg);
        Matcher m = pattern.matcher(string);
        if (m.find()) {
            string = string.replaceAll("地址", "");
            string = string.replaceAll(":", "");
            return string;
        }
        return null;
    }

    private static String isTitles(String string) {
        String titles[] = {
                "经理", "销售", "主管", "教授", "学生", "董事长", "秘书", "助理", "主任", "部长", "厅长", "工程师", "司机", "工程师"
        };
        for (int i = 0; i < titles.length; i++) {
            if (string.contains(titles[i])) {
                return string;
            }
        }
        return null;
    }

    //    private static String isPhone(char[] s) {
//        String result="";
//        int passIndex=-1;//需要先找到第一个数字的下标
//        boolean first=true;
//        for(int i=0;i<s.length;i++){
//            if(s[i]>=48&&s[i]<=57) { //看是否刚好连续11位？
//                if(first) {
//                    passIndex=i;
//                    first=false;
//                    result+=s[i];
//                    continue;
//                }
//                if((i-passIndex)==1) {//判断是否是连续 i与passindex的差值如果是1的话，则表示连续 计数+1且修改passIndex的位置
//                    passIndex = i;
//                    result+=s[i];
//                }else result="";
//            }
//        }
//        return result;
//    }
    private static String isMail(String string) {

        if(string.contains("@")||string.contains("邮箱")) {
            string = string.replaceAll("'", ".");

            String regex = ".*\\@\\w+\\.(com|cn|COM|Com)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(string);
            if (matcher.find()||string.contains("邮箱")) {
                string = string.replaceAll("邮箱", "");
                string = string.replaceAll(":", "");
                return string;
            }
        }
        return null;
    }

    private static String isPhone(String string) {
        if (string.contains("电话") || string.contains("手机")) {
            string = string.replaceAll("电话", "");
            string = string.replaceAll(":", "");
            string = string.replaceAll("手机", "");
            return string;
        }
        String reg = ".*((13[0-9])|(14[5-7])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$";
        Pattern pattern = Pattern.compile(reg);
        Matcher m = pattern.matcher(string);
        if (m.find()) {
            string = string.replaceAll("电话", "");
            string = string.replaceAll(":", "");
            string = string.replaceAll("手机", "");
            return string;
        }
        return null;
    }
}

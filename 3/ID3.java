
/**
 * Created by imcczy on 2015/11/18.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ID3 {
    private ArrayList<String> attribute = new ArrayList<String>(); // 存储属性的名称
    private ArrayList<ArrayList<String>> attributevalue = new ArrayList<ArrayList<String>>(); // 存储每个属性的取值
    private ArrayList<String[]> data = new ArrayList<String[]>(); // 原始数据
    int decatt; // 决策变量在属性集中的索引
    public static final String patternString = "@attribute(.*)[{](.*?)[}]";

    public static void main(String[] args) {
        ID3 inst = new ID3();
        inst.readARFF(new File("weather.arff"));
        inst.setDec("play");
        //属性索引
        LinkedList<Integer> ll=new LinkedList<Integer>();
        for(int i=0;i<inst.attribute.size();i++){
            if(i!=inst.decatt)
                ll.add(i);
        }
        //数据索引
        ArrayList<Integer> al=new ArrayList<Integer>();
        for(int i=0;i<inst.data.size();i++){
            al.add(i);
        }

        inst.buildtree(al,ll);
    }

    public void buildtree(ArrayList<Integer> subset,LinkedList<Integer> selatt){
        if (selatt.size() == 0) {
            getMax(subset);
            return;
        }
        if (infoPure(subset)) {
            System.out.println();
            return;
        }
        int minIndex = -1;
        double minEntropy = Double.MAX_VALUE;
        for (int i = 0; i < selatt.size(); i++) {
            if (i == decatt)
                continue;
            double entropy = calNodeEntropy(subset, selatt.get(i));
            if (entropy < minEntropy) {
                minIndex = selatt.get(i);
                minEntropy = entropy;
            }
        }
        System.out.println(attribute.get(minIndex)+":\t");
        selatt.remove(new Integer(minIndex));
        ArrayList<String> attvalues = attributevalue.get(minIndex);
        for (String val : attvalues) {

            ArrayList<Integer> al = new ArrayList<Integer>();
            for (int i = 0; i < subset.size(); i++) {
                if (data.get(subset.get(i))[minIndex].equals(val)) {
                    al.add(subset.get(i));
                }
            }
            System.out.print(val+":\t");
            buildtree(al,selatt);
        }
    }

    //读取arff文件，给attribute、attributevalue、data赋值
    public void readARFF(File file) {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            Pattern pattern = Pattern.compile(patternString);
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    attribute.add(matcher.group(1).trim());
                    String[] values = matcher.group(2).split(",");
                    ArrayList<String> al = new ArrayList<String>(values.length);
                    for (String value : values) {
                        al.add(value.trim());
                    }
                    attributevalue.add(al);
                } else if (line.startsWith("@data")) {
                    while ((line = br.readLine()) != null) {
                        if(line=="")
                            continue;
                        String[] row = line.split(",");
                        data.add(row);
                    }
                } else {
                    continue;
                }
            }
            br.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    //设置决策变量
    public void setDec(int n) {
        if (n < 0 || n >= attribute.size()) {
            System.err.println("决策变量指定错误。");
            System.exit(2);
        }
        decatt = n;
    }
    public void setDec(String name) {
        int n = attribute.indexOf(name);
        setDec(n);
    }
    public double calNodeEntropy(ArrayList<Integer> subset, int index) {
        int sum = subset.size();
        int indexsize = attributevalue.get(index).size();
        int decattsize = attributevalue.get(decatt).size();
        double entropy = 0.0;
        int[][] info = new int[indexsize][decattsize];
        int[] count = new int[indexsize];
        for (int i = 0; i < sum; i++) {
            int n = subset.get(i);
            String nodevalue = data.get(n)[index];
            int nodeind = attributevalue.get(index).indexOf(nodevalue);
            count[nodeind]++;
            String decvalue = data.get(n)[decatt];
            int decind = attributevalue.get(decatt).indexOf(decvalue);
            info[nodeind][decind]++;
        }
        for (int i = 0; i < info.length; i++) {
            entropy += getEntropy(info[i]) * count[i] / sum;
        }
        return entropy;
    }

    //给一个样本（数组中是各种情况的计数），计算它的熵
    public double getEntropy(int[] arr) {
        double entropy = 0.0;
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            entropy -= arr[i] * Math.log(arr[i]+Double.MIN_VALUE)/Math.log(2);
            sum += arr[i];
        }
        entropy += sum * Math.log(sum+Double.MIN_VALUE)/Math.log(2);
        entropy /= sum;
        return entropy;
    }

    //给一个样本数组及样本的算术和，计算它的熵
    public double getEntropy(int[] arr, int sum) {
        double entropy = 0.0;
        for (int i = 0; i < arr.length; i++) {
            entropy -= arr[i] * Math.log(arr[i]+Double.MIN_VALUE)/Math.log(2);
        }
        entropy += sum * Math.log(sum+Double.MIN_VALUE)/Math.log(2);
        entropy /= sum;
        return entropy;
    }

    public boolean infoPure(ArrayList<Integer> subset) {
        String value = data.get(subset.get(0))[decatt];
        for (int i = 1; i < subset.size(); i++) {
            String next=data.get(subset.get(i))[decatt];
            if (!value.equals(next))
                return false;
        }
        System.out.print(value+"\t");
        return true;
    }

    //当属性列表为空时，设为树叶节点，一般类
    public void getMax(ArrayList<Integer> subset){
        int maxindex =0;
        int length = attributevalue.get(decatt).size();
        int []count =new int [length];
        int sum = subset.size();
        for (int i = 0; i < sum; i++){
            int n = subset.get(i);
            String decvalue = data.get(n)[decatt];
            count[attributevalue.get(decatt).indexOf(decvalue)] ++;
        }
        for (int i =0; i < length-1;i++ ){
            if (count[i] < count[i+1])
                maxindex = i+1;
        }
        String value = attributevalue.get(decatt).get(maxindex);
        System.out.print(value+"\t");
    }
}
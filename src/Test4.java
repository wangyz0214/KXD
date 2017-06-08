


import java.util.Arrays;

public class Test4 {
/**
 * 冒泡排序，插入排序，选择排序，都要使用两个for循环，
 * 冒泡排序的外层循环只是用来控制循环次数，来保证全部的数都能比较过
 * 插入排序的外层循环用来保证数组中的第二个数开始后的每个数都能拿出来进行比较
 * 选择排序的外层循环用来确保每次跟后面的数（内层循环数）比较的都是数组中的那个位置上的数
 */
	public static void main(String[] args) {
		
		int[] ary={12,23,6,2,101,36,35,25,55};
		System.out.println(Arrays.toString(ary));
		int tmp = 0;
		for(int i=0; i<ary.length-1; i++){
			for(int j=0; j<ary.length-i-1;i++){
				if(ary[j]>ary[j+1]){
					tmp = ary[j];
					ary[j] = ary[j+1];
					ary[j+1] = tmp;
				}
			}
			System.out.println(Arrays.toString(ary));
		}
		
//		//冒泡排序 两两进行比较，左边的比右边大，就进行互换，否则不动
//		//冒泡排序的外层循环只是用来控制循环次数，来保证全部的数都能比较过
//		for(int i=0;i<ary.length-1;i++){
//			for(int j=0;j<ary.length-i-1;j++){
//				if(ary[j]>ary[j+1]){
//					int t=ary[j];
//					ary[j]=ary[j+1];
//					ary[j+1]=t;
//				}
//			}
//		}
//		System.out.println(Arrays.toString(ary));
		
		
		//插入排序 
		//取出一个数，跟前面的数进行比较，如果比前面的小就互换，互换后继续跟前面的比较，直到比前面的数大为止
		//取出的值我们要赋给一个变量，用来保证跟前面的值比较互换后，取出的这个值没有变动，如果不赋给一个变量，取出的值在互换后变掉
//		for(int i=1;i<ary.length;i++){//因为要跟前面的数进行比较，所以一定要从第二个数开始取
//			//先取出一个数
//			int k=ary[i];
//			int j;
//			for(j=i-1;j>=0;j--){//取出的数
//				if(k>ary[j]){
//					continue;
//				}
//				int t=ary[j+1];
//				ary[j+1]=ary[j];
//				ary[j]=t;
//			}
//		}
//		System.out.println(Arrays.toString(ary));
		
		
//		//选择排序
//		//1,把一个数跟后面的所有数进行比较，如果a>b,就把a跟b进行互换，然后继续用b跟后面的数进行比较，比较到最后为止，
//		//2,数组中的每个数都要进行上诉1的方式来一次
//		//这样就相当于第一轮能取出最小的数，第二轮取出第二小的。。。。。循环
//		for(int i=0;i<ary.length;i++){
//			for(int j=i+1;j<ary.length;j++){
//				if(ary[i]>ary[j]){
//					int k=ary[i];
//					ary[i]=ary[j];
//					ary[j]=k;
//				}
//			}
//		}
//		System.out.println(Arrays.toString(ary));
		
		
	}
}




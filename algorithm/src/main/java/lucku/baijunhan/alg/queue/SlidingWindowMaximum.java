package lucku.baijunhan.alg.queue;

import java.util.LinkedList;

/**
 * 239. 滑动窗口最大值
 * 给你一个整数数组 nums，有一个大小为 k 的滑动窗口从数组的最左侧移动到数组的最右侧。你只可以看到在滑动窗口内的 k 个数字。滑动窗口每次只向右移动一位。
 *
 * 返回滑动窗口中的最大值。
 *
 *
 *
 * 示例 1：
 *
 * 输入：nums = [1,3,-1,-3,5,3,6,7], k = 3
 * 输出：[3,3,5,5,6,7]
 * 解释：
 * 滑动窗口的位置                最大值
 * ---------------               -----
 * [1  3  -1] -3  5  3  6  7       3
 *  1 [3  -1  -3] 5  3  6  7       3
 *  1  3 [-1  -3  5] 3  6  7       5
 *  1  3  -1 [-3  5  3] 6  7       5
 *  1  3  -1  -3 [5  3  6] 7       6
 *  1  3  -1  -3  5 [3  6  7]      7
 * 示例 2：
 *
 * 输入：nums = [1], k = 1
 * 输出：[1]
 * 示例 3：
 *
 * 输入：nums = [1,-1], k = 1
 * 输出：[1,-1]
 * 示例 4：
 *
 * 输入：nums = [9,11], k = 2
 * 输出：[11]
 * 示例 5：
 *
 * 输入：nums = [4,-2], k = 2
 * 输出：[4]
 *
 *
 * 提示：
 *
 * 1 <= nums.length <= 105
 * -104 <= nums[i] <= 104
 * 1 <= k <= nums.length
 */
public class SlidingWindowMaximum {

    static class Solution {
        public int[] maxSlidingWindow(int[] nums, int k) {
            LinkedList<Integer> maxQueue = new LinkedList<>();
            for(int i = 0; i < k; i++){
                add(maxQueue, nums[i]);
            }
            int[] ret = new int[nums.length - k + 1];
            ret[0] = maxQueue.peekFirst();
            for(int i = 1, a = 1, b = k; b < nums.length; i++, a++, b++){
                if(maxQueue.peekFirst() == nums[a-1]){
                    maxQueue.pollFirst();
                }
                add(maxQueue, nums[b]);
                ret[i] = maxQueue.peekFirst();
            }
            return ret;
        }

        void add(LinkedList<Integer> queue, int val){
            while(!queue.isEmpty() && queue.peekLast() < val){
                queue.pollLast();
            }
            queue.offer(val);
        }
    }

}

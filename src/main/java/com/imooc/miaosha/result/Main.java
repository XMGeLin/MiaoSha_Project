package com.imooc.miaosha.result;

public class Main {
     class TreeNode{
         int val;
         TreeNode left;
         TreeNode right;
     }

    public static void main(String[] args) {

    }

    public static boolean isTreeNode(TreeNode root){
        boolean res=issTreeNode(root.left,root.right);
        return res;
    }
    public static boolean issTreeNode(TreeNode left,TreeNode right){
         if(left==null && right==null){
             return true;
         }
        if(left==null || right==null){
            return false;
        }
         if(left.val==right.val){
             return true;
         }
         return issTreeNode(left.left,right.right) && issTreeNode(left.right,right.left);
    }


}

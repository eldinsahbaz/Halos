package com.example.brian.halos;

/**
 * Created by brian on 2/9/17.
 */

public class Rating {
//    class Rating():
//    __Sum = None
//            __Votes = None
//    __Average = None
    int sum;
    int votes;
    double average;

//        def __init__(self):
//    self.__Sum = self.__Votes = self.__Average = 0.0
    public void Rating() {
        sum = 0;
        average = 0;
        votes = 0;
    }

    public void Rating(int s, int a, int v) {
        sum = s;
        average = a;
        votes = v;
    }

//    def AddVote(self, vote):
//            if isinstance(vote, type(int())):
//            if vote <= 10 and vote >= 0:
//    self.__Votes += 1
//    self.__Sum += vote
//    self.__Average = self.__Sum / self.__Votes
//    else:
//            return Exception("vote is either less than 0 or greater than 10")
//    else:
//            return Exception("invalid input")
    public void addVote() {
        sum++;
        votes++;
        average = ((double) sum) / votes;
    }

//    def GetAverage(self):
//            return self.__Average
    public double getAverage() {
        return average;
    }
}

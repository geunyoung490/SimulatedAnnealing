# SimulatedAnnealing
##### 모의 담금질(Simulated Annealing, SA)

- **전역 최적화(global optimization)** 문제에 대한 일반적인 **확률적** 메타 알고리즘이다.

- 광대한 **탐색 공간** 안에서 주어진 함수의 **전역 최적점(global optimum)**에 대한 훌륭한 근사치를 찾아준다.

- 결정체(crystals)의 크기를 크게하고 결함(defects)을 작게 하려고 금속에 열을 가하고 냉각시키는 속도를 조절하는 기술에서 따온 것이다.

- ![1](https://user-images.githubusercontent.com/80590172/121699009-85828600-cb09-11eb-8e68-b29ffec03fed.PNG)


  - 각 점은 후보해이고 아래쪽에 위치한 해가 위쪽에 있는 해보다 우수한 해이다.

  - 2개의 후보해 사이의 화살표는 이 후보해들이 서로 이웃하는 관계임을 보여준다.

  - 처음 도착한 골짜기(local optimun)에서 더 이상 아래로 탐색할 수 없는 상태에 이르렀을 때 **'운 좋게'** 위 방향으로 탐색하다가 전역 최적해를 찾은 것을 보여준다.

    --> **항상 전역 최적해를 찾아주지 않는다.**

- 하나의 초기해로부터 탐색이 진행된다.

---

### 1.  3차함수의 전역 최적점을 찾을 수 있는 모의담금질 기법을 구현해보자.

#### 모의 담금질 코드

```java
public interface Problem {
    double fit(double x);
    boolean isNeighborBetter(double f0, double f1);
}
```

- 인터페이스에 적합도를 구해주는 ```fit함수```와 이웃해가 더 적합한지 판별해주는 ```isNeighborBetter함수```를 넣는다.

```java
import java.util.*;

public class SimulatedAnnealing {
    //매개변수: 초기온도T, 후보해x0, 후보해의 적합도 f0, kt->시간에 따라 변함, niter,p
    private int niter;
    public ArrayList<Double> hist;// 후보해의 적합도 저장하는 arrylist

    public SimulatedAnnealing(int niter) {//생성자
        this.niter = niter;
        hist = new ArrayList<>();//초기화
    }
    public double solve(Problem p, double t, double a, double x0, double lower, double upper) {
        Random r = new Random();
        double f0 = p.fit(x0);
        hist.add(f0);
        
        //REPEAT
        for (int i=0; i<niter; i++) {
            int kt = (int) t; //온도가 낮아지면 횟수가 줄어듬
            for(int j=0; j<kt; j++) {
                double x1 = r.nextDouble() * (upper - lower) + lower;//이웃해 x1
                double f1 = p.fit(x1);//이웃해의 적합도 f1

                if(p.isNeighborBetter(f0, f1)) {//if(f1 > f0) 이웃해가 더 좋은 경우
                    x0 = x1;
                    f0 = f1;
                    hist.add(f0);
                }
                else {
                    double d = Math.sqrt(Math.abs(f1 - f0));
                    double p0 = Math.exp(-d/t);
                    if(r.nextDouble() < p0) {//이웃해가 더 좋지 않은데도 불구하고
                        x0 = x1;
                        f0 = f1;
                        hist.add(f0);
                    }
                }
            }
            t *= a;//0<a<1(a:냉각율, cooling ratio)
        }
        return x0;
    }
```

- 모의 담금질 알고리즘

```java
public class Main {

    public static void main(String[] args) {
        SimulatedAnnealing sa = new SimulatedAnnealing(1000);//niter = 1000
        Problem p = new Problem() {
            @Override
            public double fit(double x) {
                return -2*x*x*x+8*x+3;
            }

            @Override
            public boolean isNeighborBetter(double f0, double f1) {
                return f0 < f1;
            }
        };
        double x = sa.solve(p, 100 , 0.99, 0, -2, 2);
        //t=100,a=0.99,x0=0,lower=-2,upper=2
        System.out.println(x);//최적해
        System.out.println(p.fit(x));//최적해의 적합도
    }
}
```

 #### 결과

- 함수 y = -2x^3+8x+3인 3차함수를 사용할 것이다.

  ![3차함수](https://user-images.githubusercontent.com/80590172/121699074-94693880-cb09-11eb-8baf-d45cb81e99d0.PNG)


- 코드 결과

![3차 최적_LI](https://user-images.githubusercontent.com/80590172/121699093-99c68300-cb09-11eb-9f7b-a2dae235f97e.jpg)

  --> 두개의 최적해 중 하나가 나왔다.

---

### 2. 가장 적합한 파라미터 값을 모의담금질 기법을 이용하여 추정하자.

##### (1) 하나의 독립변수로 설명되는 종속변수 데이터

- 온도에 대한 설탕물 굴절율 데이터를 활용할 것이다.
  - **독립변수 x** : 온도
  - **종속변수 y** : 설탕물의 굴절율
- 설탕의 질량은 50%, 온도 25도~39도

![굴절율](https://user-images.githubusercontent.com/80590172/121699133-a21ebe00-cb09-11eb-93db-3d83816e33a3.PNG)


##### (2) 모의담금질 코드에 대입

- 온도에 대한 설탕물 굴절율은 **선형적인 1차함수**이다.  --> **y = ax + b**
- curve fitting  - 선형 회귀분석(Linear Regression)

![선형회귀](https://user-images.githubusercontent.com/80590172/121699177-acd95300-cb09-11eb-9088-4fe160c41686.PNG)

  - 빨간색 점이 주어진 데이터이고, 파란색 선이 주어진 데이터를 가장 잘 나타내는 직선식이다.

  - 모의담금질 코드 main에서 fit(double x) 함수를 Sr/M으로 바꾼다. 이때 M= 데이터수이다. ![오차2](https://user-images.githubusercontent.com/80590172/121699241-b9f64200-cb09-11eb-9656-99a86513d65f.PNG)
![식](https://user-images.githubusercontent.com/80590172/121699292-c2e71380-cb09-11eb-817f-3e44b582e7e1.PNG)![식2](https://user-images.githubusercontent.com/80590172/121699302-c5e20400-cb09-11eb-977e-a54a99b6d7d7.PNG)





### 3.  결과 그래프와 성능분석

![image](https://user-images.githubusercontent.com/80590172/121699359-d2665c80-cb09-11eb-811d-2d7e8a6ed85e.png)


- 각 점은 데이터 값이고 **점선이 추정된 결과 1차 직선**이다. --> **y = 0.0866x - 1.7781**

- niter값이 너무 작으면 성능이 훨씬 낮아지고 niter값이 점점 커지면 성능 값도 또한 좋아진다.
- lower과 upper의 값내에 최적해가 존재하지 않을 수 있으므로 구간도 잘 정해주어야 한다.








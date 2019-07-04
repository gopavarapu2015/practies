package capital.clix.los;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

class A {
  int a;

  public int getA() {
    return a;
  }

  public void setA(int a) {
    this.a = a;
  }
}


class B {
  int a;

  public int getA() {
    return a;
  }

  public void setA(int a) {
    this.a = a;
  }


}


public class RxTest {

  private static String milli = "1549346581495";

  public static Integer getInteger(String str) throws Exception {
    return Integer.valueOf(str);
  }

  public static void main(String[] args) throws IOException {

    ZoneId zoneid1 = ZoneId.of("Asia/Kolkata");

    LocalDateTime id1 = LocalDateTime.now(zoneid1);

    ZonedDateTime zdt = id1.atZone(ZoneId.systemDefault());

    Date output = Date.from(zdt.toInstant());

    Calendar cal = Calendar.getInstance();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String date = sdf.format(cal.getTime());

    System.out.println(date);

    // Path path = Paths.get("/home/CLIX/100405/hello.xlsx");
    // byte[] data = Files.readAllBytes(path);
    //
    // String base64Encoded = Base64.getEncoder().encodeToString(data);
    //
    // System.out.println(base64Encoded);


    // ArrayList li = new ArrayList<>();
    // li.add(1);
    // li.add(2);
    //
    // Observable.just(1, 2, 3, 4, 5).filter(i -> i % 2 == 0).subscribe(i -> System.out.println(i));
    // Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
    //
    // @Override
    // public void subscribe(ObservableEmitter<String> emitter) throws Exception {
    // emitter.onNext("A");
    // emitter.onNext("B");
    // emitter.onNext("C");
    //
    // }
    // });
    // Observer<String> observer = new Observer<String>() {
    //
    // @Override
    // public void onSubscribe(Disposable d) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void onNext(String t) {
    // System.out.println(Thread.currentThread().getName());
    // System.out.println(t);
    //
    // }
    //
    // @Override
    // public void onError(Throwable e) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void onComplete() {
    // // TODO Auto-generated method stub
    //
    // }
    // };
    // Observer<Object> observer1 = new Observer<Object>() {
    //
    // @Override
    // public void onSubscribe(Disposable d) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void onNext(Object t) {
    // System.out.println("t-----" + t);
    //
    // }
    //
    // @Override
    // public void onError(Throwable e) {
    // e.printStackTrace();
    //
    // }
    //
    // @Override
    // public void onComplete() {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // };
    //
    // Observable.just("abc", "2").map(s -> {
    // try {
    // return getInteger(s);
    // } catch (Throwable e) {
    // // throw Exceptions.propagate(e);
    // return Observable.error(e);
    // }
    //
    // }).subscribe(observer1);
    // // Executor exec = Executors.newFixedThreadPool(3);
    // //
    // // observable.subscribeOn(Schedulers.io()).subscribe(observer);
    // // observable.subscribe(observer1);
    // System.out.println(Thread.currentThread().getName());
    // System.out.println("Done");
  }
}

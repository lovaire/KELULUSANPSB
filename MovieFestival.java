import java.io.*;
import java.util.*;

public class MovieFestival {
    public static final int MOD = 1000000007;
    public static int N, M, T;
    public static int[][] movies;
    public static Moviegoer[] moviegoers;
    public static int[] moviePriority;
    public static List<Booking>[] bookingQueue;
    public static int[] movieCount;

    public static void main(String[] args) {
        FastReader in = new FastReader();
        PrintWriter out = new PrintWriter(System.out);

        N = in.nextInt();
        movies = new int[N * 3][3];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < 3; j++) {
                movies[i * 3 + j][0] = in.nextInt();
                movies[i * 3 + j][1] = in.nextInt();
                movies[i * 3 + j][2] = in.nextInt();
            }
        }

        M = in.nextInt();
        moviegoers = new Moviegoer[M];
        for (int i = 0; i < M; i++) {
            String type = in.next();
            int money = in.nextInt();
            moviegoers[i] = new Moviegoer(i, type.equals("M"), money);
        }

        T = in.nextInt();
        moviePriority = new int[N * 3];
        bookingQueue = new List[N * 3];
        for (int i = 0; i < N * 3; i++) {
            moviePriority[i] = (int) Math.ceil(movies[i][1] * movies[i][2] / 100.0);
            bookingQueue[i] = new ArrayList<>();
        }

        movieCount = new int[M];
        for (int i = 0; i < T; i++) {
            String op = in.next();
            if (op.equals("B")) {
                int moviegoerId = in.nextInt() - 1;
                int movieId = in.nextInt() - 1;
                bookMovie(moviegoers[moviegoerId], movieId);
                out.println(bookingQueue[movieId].size());
            } else if (op.equals("P")) {
                int movieId = in.nextInt() - 1;
                playMovie(movieId, out);
            } else if (op.equals("T")) {
                int moviegoerId = in.nextInt() - 1;
                int movieId = in.nextInt() - 1;
                trackBooking(moviegoers[moviegoerId], movieId, out);
            } else if (op.equals("J")) {
                int type = in.nextInt();
                joinFestival(type, out);
            }
        }

        out.flush();
    }

    public static void bookMovie(Moviegoer moviegoer, int movieId) {
        if (moviegoer.money >= movies[movieId][0]) {
            List<Booking> queue = bookingQueue[movieId];
            int priority = moviegoer.isMember ? 0 : moviePriority[movieId];
            queue.add(new Booking(moviegoer.id, moviegoer.count, priority));
            queue.sort(Comparator.comparing((Booking b) -> b.priority)
                               .thenComparing(b -> b.count)
                               .thenComparing(b -> b.id));
        }
    }

    public static void playMovie(int movieId, PrintWriter out) {
        List<Booking> queue = bookingQueue[movieId];
        if (queue.isEmpty()) {
            out.println(-1);
            return;
        }

        int capacity = movies[movieId][1];
        int price = movies[movieId][0];
        for (int i = 0; i < Math.min(capacity, queue.size()); i++) {
            Booking booking = queue.get(i);
            Moviegoer moviegoer = moviegoers[booking.id];
            moviegoer.money -= price;
            moviegoer.count++;
            movieCount[moviegoer.id]++;
            out.print((moviegoer.id + 1) + " ");
        }
        out.println();
        queue.clear();
    }

    public static void trackBooking(Moviegoer moviegoer, int movieId, PrintWriter out) {
        List<Booking> queue = bookingQueue[movieId];
        int index = -1;
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).id == moviegoer.id) {
                index = i + 1;
                break;
            }
        }
        out.println(index);
    }

    public static void joinFestival(int type, PrintWriter out) {
        if (type == 0) {
            int[] genres = new int[3];
            genres[0] = genres[1] = genres[2] = -1;
            int maxPrice = 0;
            for (int i = 0; i < N; i++) {
                int[] prices = new int[3];
                for (int j = 0; j < 3; j++) {
                    prices[j] = movies[i * 3 + j][0];
                }
                Arrays.sort(prices);
                int currPrice = 0;
                for (int j = 2; j >= 0; j--) {
                    if (prices[j] > genres[j] || (prices[j] == genres[j] && genres[j] == -1)) {
                        genres[j] = prices[j];
                        currPrice += prices[j];
                    }
                }
                if (currPrice > maxPrice) {
                    maxPrice = currPrice;
                }
            }
            out.println(maxPrice);
        } else {
            int[] genres = new int[N];
            int maxPrice = 0;
            for (int i = 0; i < N; i++) {
                int[] prices = new int[3];
                for (int j = 0; j < 3; j++) {
                    prices[j] = movies[i * 3 + j][0];
                }
                Arrays.sort(prices);
                int currPrice = 0;
                int[] currGenres = new int[3];
                for (int j = 2; j >= 0; j--) {
                    if (prices[j] > currGenres[j]) {
                        currGenres[j] = prices[j];
                        currPrice += prices[j];
                    }
                }
                if (currPrice > maxPrice) {
                    maxPrice = currPrice;
                    for (int j = 0; j < 3; j++) {
                        genres[i] = findMovieIndex(currGenres[j]) + 1;
                    }
                } else if (currPrice == maxPrice) {
                    int[] tempGenres = new int[N];
                    for (int j = 0; j < 3; j++) {
                        tempGenres[i] = findMovieIndex(currGenres[j]) + 1;
                    }
                    if (compareLexicographically(tempGenres, genres)) {
                        System.arraycopy(tempGenres, 0, genres, 0, N);
                    }
                }
            }
            for (int i = 0; i < N; i++) {
                out.print(genres[i] + " ");
            }
            out.println();
        }
    }

    public static int findMovieIndex(int price) {
        for (int i = 0; i < movies.length; i++) {
            if (movies[i][0] == price) {
                return i;
            }
        }
        return -1;
    }

    public static boolean compareLexicographically(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] < b[i]) {
                return true;
            } else if (a[i] > b[i]) {
                return false;
            }
        }
        return false;
    }

    static class Moviegoer {
        int id;
        boolean isMember;
        int money;
        int count;

        Moviegoer(int id, boolean isMember, int money) {
            this.id = id;
            this.isMember = isMember;
            this.money = money;
            this.count = 0;
        }
    }

    static class Booking {
        int id;
        int count;
        int priority;

        Booking(int id, int count, int priority) {
            this.id = id;
            this.count = count;
            this.priority = priority;
        }
    }

    static class FastReader {
        BufferedReader br;
        StringTokenizer st;

        FastReader() {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        String next() {
            while (st == null || !st.hasMoreElements()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
    }
}
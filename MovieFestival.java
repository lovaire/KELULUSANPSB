import java.util.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;

class MovieFestival {
    static class Moviegoer {
        int id, money; 
        boolean isMember;
        int watchedMovies;
        
        Moviegoer(int id, boolean isMember, int money) {
            this.id = id;
            this.isMember = isMember;
            this.money = money;
            this.watchedMovies = 0;  
        }
    }
    
    static class Movie {
        int id, price, capacity, memberPercentage; 
        Queue<Moviegoer> queue = new LinkedList<>();
        
        Movie(int id, int price, int capacity, int memberPercentage) {
            this.id = id;
            this.price = price;    
            this.capacity = capacity;
            this.memberPercentage = memberPercentage;
        }
        
        void book(Moviegoer m) {
            if (m.money >= price) {
                queue.add(m);
            }
        }
        
        void play() {
            if (queue.isEmpty()) {
                System.out.println(-1);
                return;
            }
            
            int memberSpots = (int)Math.ceil(capacity * (memberPercentage/100.0)); 
            Stack<Integer> viewers = new Stack<>();
            
            while (!queue.isEmpty() && viewers.size() < capacity) {
                Moviegoer m = queue.poll();
                viewers.push(m.id);
                m.watchedMovies++;
                m.money -= price;  
            }
            
            while (!viewers.isEmpty()) {
                System.out.print(viewers.pop() + " "); 
            }
            System.out.println();
        }
        
        int getViewerPos(Moviegoer m) {
            if (!queue.contains(m)) return -1;
            
            int pos = 1;
            for (Moviegoer viewer : queue) {
                if (viewer == m) return pos;
                pos++;
            }
            return -1;
        }
    }
    
    static void joinFestival(int N, List<Movie> movies, List<Moviegoer> moviegoers, int type, int day) {
        if (type == 0) {
            int[] maxPrices = new int[N];
            int[] genres = new int[N];
            
            maxPrices[0] = Integer.MIN_VALUE;
            for (Movie m : movies) {
                if (m.price > maxPrices[0]) {
                    genres[0] = m.id % 3 + 1; 
                    maxPrices[0] = m.price;
                }
            }
            
            for (int i=1; i<N; i++) {
                maxPrices[i] = Integer.MIN_VALUE;
                
                for (Movie m : movies) { 
                    if (genres[i-1] != m.id % 3 + 1 && m.price > maxPrices[i]) {
                        genres[i] = m.id % 3 + 1;
                        maxPrices[i] = m.price;    
                    }
                }
            }
            
            int total = 0;
            for (int price : maxPrices) total += price;
            
            System.out.println(total);
            
        } else {
            int[] maxPrices = new int[N]; 
            int[][] possibilities = new int[N][N];
            
            maxPrices[0] = Integer.MIN_VALUE;
            for (Movie m : movies) {
                if (m.price > maxPrices[0]) {
                    possibilities[0][0] = m.id % 3 + 1;  
                    maxPrices[0] = m.price;
                }
            }
            
            for (int i=1; i<N; i++) {
            
                maxPrices[i] = Integer.MIN_VALUE;
                int index = 0;
                
                for (Movie m : movies) {
                
                    boolean valid = true;
                    if (i > 0) {
                        for (int j=0; j<i; j++) {
                            if (possibilities[j][index] == m.id % 3 + 1) {
                                valid = false;
                                break;
                            }
                        } 
                    }
                
                    if (valid && m.price > maxPrices[i]) {
                        possibilities[i][index] = m.id % 3 + 1;
                        maxPrices[i] = m.price;    
                    }
                }
                index++;
            }
            
            int maxTotal = 0;
            int[] sequence = new int[N];
            
            for (int i=0; i<possibilities[0].length; i++) {
                int total = maxPrices[0];
                sequence[0] = possibilities[0][i];
                
                for (int j=1; j<N; j++) {   
                    boolean found = false;
                    
                    for (int k=0; k<possibilities[j].length; k++) {
                    
                        boolean valid = true;
                        for (int l=0; l<j; l++) {
                            if (sequence[l] == possibilities[j][k]) {
                                valid = false;  
                                break; 
                            }
                        }
                        
                        if (valid) {
                            sequence[j] = possibilities[j][k];
                            total += maxPrices[j];
                            found = true;
                            break;
                        }
                    }
                    
                    if (!found) break;
                    
                }
                
                if (total > maxTotal) {
                    maxTotal = total;
                } 
            }
                
            for (int genre : sequence) System.out.print(genre + " ");
            System.out.println();
            
        }
        
    }
    
    public static void main(String[] args) {
    
        Scanner sc = new Scanner(System.in);
    
        int days = sc.nextInt();
        
        List<Movie> horrorMovies = new ArrayList<>(); 
        List<Movie> actionMovies = new ArrayList<>();
        List<Movie> scifiMovies = new ArrayList<>();

        List<Movie> movies = Stream.of(horrorMovies, actionMovies, scifiMovies)
                                   .flatMap(List::stream)
                                   .collect(Collectors.toList());
        
        for (int i=0; i<days; i++) {
            int price = sc.nextInt(); 
            int capacity = sc.nextInt();
            int percentage = sc.nextInt();
            horrorMovies.add(new Movie(i+1, price, capacity, percentage)); 
        }
        
        for (int i=0; i<days; i++) {
            int price = sc.nextInt();
            int capacity = sc.nextInt(); 
            int percentage = sc.nextInt();
            actionMovies.add(new Movie(i+days+1, price, capacity, percentage));
        }
        
        for (int i=0; i<days; i++) {
            int price = sc.nextInt();
            int capacity = sc.nextInt();
            int percentage = sc.nextInt();
            scifiMovies.add(new Movie(i+days*2+1, price, capacity, percentage)); 
        }
        
        int numMoviegoers = sc.nextInt();
        
        List<Moviegoer> moviegoers = new ArrayList<>(); 
        for (int i=0; i<numMoviegoers; i++) {
            String type = sc.next();
            int money = sc.nextInt();
            
            boolean isMember = type.equals("M"); 
            moviegoers.add(new Moviegoer(i+1, isMember, money));
        }
        
        int queries = sc.nextInt();
        
        while (queries-- > 0) {

            String action = sc.next();
        
            if (action.equals("J")) {
                
                int type = sc.nextInt();
                int day = sc.nextInt();
                
                joinFestival(days, movies, moviegoers, type, day);
            
            } else if (action.equals("B")) {
                
                int moviegoerId = sc.nextInt() - 1;
                int movieId = sc.nextInt() - 1;
                
                Movie movie;
                if (movieId < days) {
                    movie = horrorMovies.get(movieId);
                } else if (movieId < 2*days) {
                    movie = actionMovies.get(movieId-days); 
                } else {
                    movie = scifiMovies.get(movieId-2*days);
                }
                
                movie.book(moviegoers.get(moviegoerId));
                System.out.println(movie.queue.size());
                
            } else if (action.equals("P")) {
                int movieId = sc.nextInt() - 1;
                
                Movie movie; 
                if (movieId < days) {
                    movie = horrorMovies.get(movieId);
                } else if (movieId < 2*days) {
                    movie = actionMovies.get(movieId-days);
                } else {
                    movie = scifiMovies.get(movieId-2*days);
                }
                
                movie.play();
                
            } else if (action.equals("T")) {
                int type = sc.nextInt();
                int moviegoerId = sc.nextInt() - 1;
                int movieId = sc.nextInt() - 1;
                
                Movie movie;
                if (movieId < days) {
                    movie = horrorMovies.get(movieId);
                } else if (movieId < 2*days) {
                    movie = actionMovies.get(movieId-days);
                } else {
                    movie = scifiMovies.get(movieId-2*days); 
                }
                
                System.out.println(movie.getViewerPos(moviegoers.get(moviegoerId)));
                 
            } else {
                int type = sc.nextInt();
                int day = sc.nextInt();

                joinFestival(days, Stream.of(horrorMovies, actionMovies, scifiMovies)
                                    .flatMap(x -> x.stream())
                                    .collect(Collectors.toList()),
                                    moviegoers,
                                    type, day);
            }
            
            
        }
        
        sc.close();
    }
}
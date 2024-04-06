import tweepy
import time

# Kunci API dan token akses Anda
consumer_key = 'consumer_key Anda'
consumer_secret = 'consumer_secret Anda'
access_token = 'access_token Anda'
access_token_secret = 'access_token_secret Anda'

# Autentikasi dengan Twitter
auth = tweepy.OAuth1UserHandler(consumer_key, consumer_secret, access_token, access_token_secret)
api = tweepy.API(auth)

# Kata kunci untuk pencarian dan akun yang ingin Anda retweet
search_keywords = ['kata kunci 1', 'kata kunci 2']
target_account = 'akun target'

# Fungsi untuk retweet
def retweet_tweets():
    tweets = api.user_timeline(screen_name=target_account, count=10)
    for tweet in tweets:
        if any(keyword in tweet.text.lower() for keyword in search_keywords):
            try:
                api.retweet(tweet.id)
                print("Retweeted: ", tweet.text)
            except tweepy.TweepError as e:
                print(e.reason)

# Fungsi untuk membuat postingan baru
def create_tweet():
    caption = "Ini adalah keterangan postingan baru."
    media_path = "lokasi/berkas/gambar.jpg"
    try:
        media = api.media_upload(media_path)
        api.update_status(status=caption, media_ids=[media.media_id])
        print("Posting baru berhasil dibuat.")
    except tweepy.TweepError as e:
        print(e.reason)

# Fungsi untuk memberikan balasan otomatis
def reply_to_tweets():
    search_results = api.search(q='kata kunci trigger', count=10)
    for tweet in search_results:
        if 'kata kunci trigger' in tweet.text.lower():
            try:
                api.update_status("Ini adalah balasan otomatis.", in_reply_to_status_id=tweet.id)
                print("Balasan otomatis berhasil dikirim.")
            except tweepy.TweepError as e:
                print(e.reason)

# Loop utama untuk menjalankan bot secara terus-menerus
while True:
    retweet_tweets()
    create_tweet()
    reply_to_tweets()
    time.sleep(300)  # Bot akan memeriksa setiap 5 menit

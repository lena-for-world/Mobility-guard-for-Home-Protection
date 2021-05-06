import pymysql
import time

def insert(value):
    num = 1
    time.sleep(0.3)
    conn = pymysql.connect(host="192.168.25.54", port=3306, user="root", passwd="1234", db="testDB", charset='utf8')
    time.sleep(1)
    cur = conn.cursor()
    value = value + 1
    num = num + 1
    temp = "CO"
    cur.execute("INSERT INTO test VALUES (%s, %s, %s)", (num, temp, value))
    conn.commit()

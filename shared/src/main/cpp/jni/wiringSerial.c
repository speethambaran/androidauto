
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdarg.h>
#include <string.h>
#include <termios.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <android/log.h>
#include <time.h>
#include <strings.h>
#include <sys/socket.h>

#include "wiringSerial.h"
#include "wiringPi.h"


int serialOpen (const char *device, const int baud)
{
  struct termios options ;
  speed_t myBaud ;
  int     status, fd ;
//  __android_log_print(ANDROID_LOG_ERROR, "at", "%s", "serialOpen");

  switch (baud)
  {
    case      50:	myBaud =      B50 ; break ;
    case      75:	myBaud =      B75 ; break ;
    case     110:	myBaud =     B110 ; break ;
    case     134:	myBaud =     B134 ; break ;
    case     150:	myBaud =     B150 ; break ;
    case     200:	myBaud =     B200 ; break ;
    case     300:	myBaud =     B300 ; break ;
    case     600:	myBaud =     B600 ; break ;
    case    1200:	myBaud =    B1200 ; break ;
    case    1800:	myBaud =    B1800 ; break ;
    case    2400:	myBaud =    B2400 ; break ;
    case    4800:	myBaud =    B4800 ; break ;
    case    9600:	myBaud =    B9600 ; break ;
    case   19200:	myBaud =   B19200 ; break ;
    case   38400:	myBaud =   B38400 ; break ;
    case   57600:	myBaud =   B57600 ; break ;
    case  115200:	myBaud =  B115200 ; break ;
    case  230400:	myBaud =  B230400 ; break ;
    case  460800:	myBaud =  B460800 ; break ;
    case  500000:	myBaud =  B500000 ; break ;
    case  576000:	myBaud =  B576000 ; break ;
    case  921600:	myBaud =  B921600 ; break ;
    case 1000000:	myBaud = B1000000 ; break ;
    case 1152000:	myBaud = B1152000 ; break ;
    case 1500000:	myBaud = B1500000 ; break ;
    case 2000000:	myBaud = B2000000 ; break ;
    case 2500000:	myBaud = B2500000 ; break ;
    case 3000000:	myBaud = B3000000 ; break ;
    case 3500000:	myBaud = B3500000 ; break ;
    case 4000000:	myBaud = B4000000 ; break ;

    default:
      return -2 ;
  }
//  __android_log_print(ANDROID_LOG_ERROR, "at", "%s", "after switch");

  if ((fd = open (device, O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK)) == -1)
//  if ((fd = open (device, O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK)) == -1)
    return -1 ;


  fcntl (fd, F_SETFL, O_RDWR) ;

// Get and modify current options:

  tcgetattr (fd, &options) ;

    cfmakeraw   (&options) ;
    cfsetispeed (&options, myBaud) ;
    cfsetospeed (&options, myBaud) ;

    options.c_cflag |= (CLOCAL | CREAD) ;
    options.c_cflag &= ~PARENB ;
    options.c_cflag &= ~CSTOPB ;
    options.c_cflag &= ~CSIZE ;
    options.c_cflag |= CS8 ;
    options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG) ;
    options.c_oflag &= ~OPOST ;

    options.c_cc [VMIN]  =   0 ;
    options.c_cc [VTIME] = 100 ;	// Ten seconds (100 deciseconds)

  tcsetattr (fd, TCSANOW, &options) ;

  ioctl (fd, TIOCMGET, &status);

  status |= TIOCM_DTR ;
  status |= TIOCM_RTS ;

  ioctl (fd, TIOCMSET, &status);

  usleep (10000) ;	// 10mS

  return fd ;
}

void serialPutchar (const int fd, const unsigned char c)
{
  write (fd, &c, 1) ;
}


int serialDataAvail (const int fd)
{
  int result ;

  if (ioctl (fd, FIONREAD, &result) == -1)
    return -1 ;

  return result ;
}

int serialGetchar (const int fd)
{
  uint8_t x ;

  if (read (fd, &x, 1) != 1)
    return -1 ;

  return ((int)x) & 0xFF ;
}
void serialGetstring( char *p, const int fd ) //created by Nithin. Aims to read String At a time
{
//  ssize_t a;
  uint8_t y=0 ;

 /* for(int i=0;i<=20;i++) {
   if (read(fd, &y, 1) != 1) {
      break;
    }else{
      p[i]= (char) (((int)y) & 0xFF) ;
//      delay_time(0.1);
    }
  }*/

  int i=0;
  char ch ;

  do {
    if (read(fd, &ch, 1) != 1) {
      break;
    }else{
      p[i]=  ch;
      i++;
//      delay_time(0.1);
    }
  }while(ch !=';');







   read(fd,p, 100);
//   recv(fd,p,100,1);

  /*a=read(fd, p, 15);
  if(a != 15){
    return a;
  }else{
    return 15;
  }*/
  /*for(int i=0;i<=15;i++) {
    p[i]=(char) serialGetchar(fd);
  }*/
  /*uint8_t y=0 ;
  char ch ;
  for(int i=0;i<=20;i++) {
    if (read(fd, &y, 1) != 1) {
      //if (read(fd, &ch, 1) != 1) {
      break;
    }else{
      p[i]= (char) (((int)y) & 0xFF) ;
//      p[i]=  (ch)  ;
//      delay_time(0.1);
    }
  }*/
}
 void delay_time(float number_of_seconds) //for giving a delay /NP

{
  // Converting time into milli_seconds
  int milli_seconds = 1000 * number_of_seconds;
  // Storing start time
  clock_t start_time = clock();
  // looping till required time is not achieved
  while (clock() < start_time + milli_seconds);
}


// for reading the string
char gBUF[BUFSIZ];

int readline2(int fd, char* buf){

  static char* resultBuffer = gBUF;
  int ret, mv;
  char temp[16];
  char* t;

  bzero(temp, sizeof(temp));



  t = strchr(resultBuffer,'\n');
  if(t != NULL){
    mv = t-resultBuffer+1;
    strncpy(temp,resultBuffer, mv);
    resultBuffer = resultBuffer + mv;

    temp[strlen(temp)] = '\0';
    strcpy(buf,temp);
    bzero(temp, sizeof(temp));
    ret = read(fd,&temp,16);
    temp[ret]='\0';
    strcat(resultBuffer,temp);

    return ret;
  }
  ret = read(fd,&temp,16);
  temp[ret]='\0';
  t = strchr(temp,'\n');
  mv = t-temp+1;
  strncpy(buf,temp,mv);

  strcat(resultBuffer,temp+mv);
  return ret;

}

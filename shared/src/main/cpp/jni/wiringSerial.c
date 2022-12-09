
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
#include <bits/threads_inlines.h>

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

  tcgetattr (fd, &options) ;  // Get the current attributes of the Serial port

    cfmakeraw   (&options) ;
    cfsetispeed (&options, myBaud) ; // Set Read  Speed as 9600
    cfsetospeed (&options, myBaud) ; // Set Write Speed as 9600

    options.c_cflag |= (CLOCAL | CREAD) ;  // Enable receiver,Ignore Modem Control lines
    options.c_cflag &= ~PARENB ; // Disables the Parity Enable bit(PARENB),So No Parity
    options.c_cflag &= ~CSTOPB ; // CSTOPB = 2 Stop bits
    options.c_cflag &= ~CSIZE ;   // Clears the mask for setting the data size
    options.c_cflag |= CS8 ;    // Set the data bits = 8
    options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG) ;
    options.c_oflag &= ~OPOST ; // No Output Processing

    options.c_cc [VMIN]  =   19 ; // added for testing // added 1 sec
    options.c_cc [VTIME] = 100 ; // added for testing // added	// Ten seconds (100 deciseconds)

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
  }while(i <2);
//  }while(ch !=';');







//   read(fd,p, 100);
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

//editted codes below
int serialOpen1 (const char *device, const int baud)
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

  tcgetattr (fd, &options) ;  // Get the current attributes of the Serial port

  cfmakeraw   (&options) ;
  cfsetispeed (&options, myBaud) ; // Set Read  Speed as 9600
  cfsetospeed (&options, myBaud) ; // Set Write Speed as 9600

  //added by nith
  options.c_cflag &= ~PARENB;   // Disables the Parity Enable bit(PARENB),So No Parity
  options.c_cflag &= ~PARODD; // added

  if (0 > tcgetattr(fd, &options))
  {
    return 4;/* log and handle error */
  }

  options.c_cflag |= CSTOPB;   // CSTOPB = 2 Stop bits
  options.c_cflag &= ~CSIZE;    // Clears the mask for setting the data size
  options.c_cflag |=  CS8;      // Set the data bits = 8


  options.c_cflag &= ~CRTSCTS;       // No Hardware flow Control
  options.c_cflag |= (CREAD | CLOCAL); // Enable receiver,Ignore Modem Control lines

  options.c_lflag =0; /* RAW input */ // added

  options.c_iflag &= ~(IXON | IXOFF | IXANY);          // Disable XON/XOFF flow control both i/p and o/p
  options.c_iflag &= ~(ICANON | ECHO | ECHOE | ISIG);  // Non Cannonical mode

  options.c_cc[VMIN]  = 14; //// Wait for at least 1 character before returning
  options.c_cc[VTIME] = 50; //// Wait 200 milliseconds between bytes before returning from read

  options.c_iflag = 0;            /* SW flow control off, no parity checks etc */ // added

  options.c_oflag &= ~OPOST;// No Output Processing
  options.c_oflag = 0;

  if((tcsetattr(fd,TCSANOW,&options)) != 0) // Set the attributes to the termios structure
    printf("\n  ERROR ! in Setting attributes");
  else
    printf("\n  BaudRate = 9600 \n  StopBits = 2 \n  Parity   = None\n");
  //------------------------------- Read data from serial port -----------------------------

  //tcflush(fd, TCIFLUSH);  //  Discards old data in the rx buffer

  // add ended....nith

//  options.c_cflag |= (CLOCAL | CREAD) ;  // Enable receiver,Ignore Modem Control lines
  //options.c_cflag &= ~PARENB ; // Disables the Parity Enable bit(PARENB),So No Parity
 // options.c_cflag &= ~CSTOPB ; // CSTOPB = 2 Stop bits
 // options.c_cflag &= ~CSIZE ;   // Clears the mask for setting the data size
 // options.c_cflag |= CS8 ;    // Set the data bits = 8
 // options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG) ;
 // options.c_oflag &= ~OPOST ; // No Output Processing

//  options.c_cc [VMIN]  =   0 ; // added for testing // added 1 sec
//  options.c_cc [VTIME] = 100 ; // added for testing // added	// Ten seconds (100 deciseconds)

 /* tcsetattr (fd, TCSANOW, &options) ;

  ioctl (fd, TIOCMGET, &status);

  status |= TIOCM_DTR ;
  status |= TIOCM_RTS ;

  ioctl (fd, TIOCMSET, &status);

  usleep (10000) ;*/	// 10mS

  return fd ;
}

void serialGetstring1( char *p, const int fd,uint8_t *rebitSiz ) //created by Nithin. Aims to read String At a time
{

  *rebitSiz = read (fd, p, 32);
  /*int i=0;
  char ch ;

  do {
    if (read(fd, &ch, 1) != 1) {
      break;
    }else{
      p[i]=  ch;
      *rebitSiz=*rebitSiz+1;
      i++;
//      delay_time(0.1);
    }
  }while(i <16);*/
//  *rebitSiz=20;
  //tcflush(fd, TCIFLUSH);
  //close(fd);// Close the serial port
}

int serialReadData (const char *device, const int baud,char *p)
{
  struct termios options ;
  speed_t myBaud=B9600 ;
  int fd ;

  if ((fd = open (device, O_RDWR | O_NOCTTY | O_NDELAY | O_NONBLOCK)) == -1)
    return -1 ;


  fcntl (fd, F_SETFL, O_RDWR) ;

// Get and modify current options:

  tcgetattr (fd, &options) ;  // Get the current attributes of the Serial port

  cfmakeraw   (&options) ;
  cfsetspeed (&options, myBaud) ; // Set Read and write  Speed as 9600

  //added by nith
  options.c_cflag &= ~PARENB;   // Disables the Parity Enable bit(PARENB),So No Parity
  options.c_cflag &= ~PARODD; // added



  options.c_cflag |= CSTOPB;   // CSTOPB = 2 Stop bits
  options.c_cflag &= ~CSIZE;    // Clears the mask for setting the data size
  options.c_cflag |=  CS8;      // Set the data bits = 8


  options.c_cflag &= ~CRTSCTS;       // No Hardware flow Control
  options.c_cflag |= (CREAD | CLOCAL); // Enable receiver,Ignore Modem Control lines

  options.c_lflag =0; /* RAW input */ // added

  options.c_iflag &= ~(IXON | IXOFF | IXANY);          // Disable XON/XOFF flow control both i/p and o/p
  options.c_iflag &= ~(ICANON | ECHO | ECHOE | ISIG);  // Non Cannonical mode

  options.c_cc[VMIN]  = 19; //// Wait for at least 1 character before returning
  options.c_cc[VTIME] = 100; //// Wait 200 milliseconds between bytes before returning from read

  options.c_iflag = 0;            /* SW flow control off, no parity checks etc */ // added

  options.c_oflag &= ~OPOST;// No Output Processing
  options.c_oflag = 0;

  if((tcsetattr(fd,TCSANOW,&options)) != 0) // Set the attributes to the termios structure
    printf("\n  ERROR ! in Setting attributes");
  else
    printf("\n  BaudRate = 9600 \n  StopBits = 2 \n  Parity   = None\n");
  //------------------------------- Read data from serial port -----------------------------
  read (fd, p, 32);
  //  recv (fd, p, 32,);
  //close(fd);// Close the serial port

  return fd ;
}


// for threadingg...

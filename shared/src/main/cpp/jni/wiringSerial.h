
#ifdef __cplusplus
extern "C" {
#endif

extern int   serialOpen      (const char *device, const int baud) ;
extern int   serialOpen1      (const char *device, const int baud) ;
extern void  serialPutchar   (const int fd, const unsigned char c) ;
extern int   serialDataAvail (const int fd) ;
extern int   serialGetchar   (const int fd) ;
extern void  serialGetstring  ( char *p, const int fd ) ;
extern void  serialGetstring1 ( char *p, const int fd,uint8_t *rebitSiz ) ;
void delay_time(float number_of_seconds);
int readline2(int fd, char* buf);
int serialReadData (const char *device, const int baud,char *p);

#ifdef __cplusplus
}
#endif
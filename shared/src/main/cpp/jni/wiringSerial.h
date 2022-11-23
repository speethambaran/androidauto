
#ifdef __cplusplus
extern "C" {
#endif

extern int   serialOpen      (const char *device, const int baud) ;
extern void  serialPutchar   (const int fd, const unsigned char c) ;
extern int   serialDataAvail (const int fd) ;
extern int   serialGetchar   (const int fd) ;
extern void  serialGetstring  ( char *p, const int fd ) ;

#ifdef __cplusplus
}
#endif
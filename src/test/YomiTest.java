p a c k a g e   t e s t ;  
  
 i m p o r t   s t a t i c   o r g . j u n i t . A s s e r t . a s s e r t E q u a l s ;  
  
 i m p o r t   j a v a . u t i l . A r r a y L i s t ;  
 i m p o r t   j a v a . u t i l . L i s t ;  
  
 i m p o r t   o r g . j u n i t . T e s t ;  
  
 i m p o r t   y o m i . Y o m i D e c o d e r ;  
  
  
 p u b l i c   c l a s s   Y o m i T e s t   {  
 	  
 	 @ T e s t  
 	 p u b l i c   v o i d   t e s t N u m b e r s A n d C o u n t e r s ( ) {  
 	 	 A r r a y L i s t < C o r r e c t Y o m i >   o C a s e s   =   n e w   A r r a y L i s t < C o r r e c t Y o m i > ( ) ;  
 	 	 o C a s e s . a d d ( n e w   C o r r e c t Y o m i ( " ’’NQ" ,   " ’’~00H00" ) ) ;  
 	 	 o C a s e s . a d d ( n e w   C o r r e c t Y o m i ( " ’’öNB00" ,   " ’’Q00B00" ) ) ;  
 	 	 o C a s e s . a d d ( n e w   C o r r e c t Y o m i ( " ’’’ŗNn02ļ" ,   " ’’’k00n0W000" ) ) ;  
 	 	 o C a s e s . a d d ( n e w   C o r r e c t Y o m i ( " ’’’’t^k0" ,   " ’’’’m00k0" ) ) ;  
 	 	 o C a s e s . a d d ( n e w   C o r r e c t Y o m i ( " t e s t " ,   " t e s t " ) ) ;  
 	 	  
 	 	 r u n T e s t L i s t ( o C a s e s ) ;  
 	 }  
  
 	 @ T e s t  
 	 p u b l i c   v o i d   t e s t S o m e N a m e s ( ) {  
 	 	 A r r a y L i s t < C o r r e c t Y o m i >   o C a s e s   =   n e w   A r r a y L i s t < C o r r e c t Y o m i > ( ) ;  
 	 	 o C a s e s . a d d ( n e w   C o r r e c t Y o m i ( " ]NµkN" ,   " O0`00W0_0" ) ) ;  
 	 	 o C a s e s . a d d ( n e w   C o r r e c t Y o m i ( " q,g" ,   " O0~00h0" ) ) ;  
 	 	 o C a s e s . a d d ( n e w   C o r r e c t Y o m i ( " ~gNx^KN©R" ,   " ~0d0W0_0S0F0n0Y0Q0" ) ) ;  
 	 	 o C a s e s . a d d ( n e w   C o r r e c t Y o m i ( " `ä" ,   " H00i0F0" ) ) ;  
 	 	 o C a s e s . a d d ( n e w   C o r r e c t Y o m i ( " ®[]’" ,   " 00V0M0o00J0" ) ) ;  
 	 	  
 	 	 r u n T e s t L i s t ( o C a s e s ) ;  
 	 }  
 	  
 	  
 	 p u b l i c   v o i d   r u n T e s t L i s t ( L i s t < C o r r e c t Y o m i >   o C a s e s ) {  
 	 	 Y o m i D e c o d e r   o D e c o d e r   =   n e w   Y o m i D e c o d e r ( ) ;  
 	 	 f o r   ( C o r r e c t Y o m i   o C a s e   :   o C a s e s ) {  
                 	 S t r i n g   d e c o d e d   =   o D e c o d e r . G e t Y o m i ( o C a s e . k a n j i ) ;  
                 	 a s s e r t E q u a l s ( d e c o d e d ,   o C a s e . y o m i ) ;  
 	 	 }  
 	 }  
 	  
 	 p u b l i c   c l a s s   C o r r e c t Y o m i {  
 	 	 p u b l i c   S t r i n g   k a n j i   =   " " ;  
 	 	 p u b l i c   S t r i n g   y o m i   =   " " ;  
 	 	 p u b l i c   C o r r e c t Y o m i ( S t r i n g   p k a n j i ,   S t r i n g   p Y o m i ) {  
 	 	 	 k a n j i   =   p k a n j i ;  
 	 	 	 y o m i   =   p Y o m i ;  
 	 	 }  
 	 }  
 	  
 }  
 
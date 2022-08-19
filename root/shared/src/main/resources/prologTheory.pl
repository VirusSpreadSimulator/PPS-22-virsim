changeX(X, List, Xn, Width) :- member(N, List), Xn is X + N, Xn >= 0, Xn =< Width.
changeY(Y, List, Yn, Height) :- member(N, List), Yn is Y + N, Yn >= 0, Yn =< Height.

newPoint(point(X, Y), Xn, Yn, List, Width, Height) :- changeX(X, List, Xn, Width), changeY(Y, List, Yn, Height),  changed(X, Xn, Y, Yn).

changed(X, Xn, Y, Yn) :- X =\= Xn, !.
changed(X, Xn, Y, Yn) :- Y =\= Yn, !.

goHome(point(X, Y), Xn, Yn, List, Width, Height, home(Xh, Yh)) :- newPoint(point(X, Y), Xn, Yn, List, Width, Height), closer(point(X,Y), point(Xn, Yn), point(Xh, Yh)).

closer(point(X,Y), point(Xn, Yn), point(Xh, Yh)) :- distance(point(X, Y), point(Xh, Yh), CurrDist), distance(point(Xn, Yn), point(Xh, Yh), NewDist), NewDist < CurrDist.

distance(point(X,Y), point(X2,Y2), Dist):- DeltaX is X2-X, DeltaY is Y2-Y, pow(DeltaX,2,PowX), pow(DeltaY,2,PowY), Dist is sqrt(PowX+PowY).

pow(X, Esp, Y):-pow(X, X, Esp, Y).
pow(X, Temp, Esp, Y):- Esp=:=0, !, Y=1.
pow(X, Temp, Esp, Y):- Esp=:=1, !, Y is Temp.
pow(X, Temp, Esp, Y):- pow(X,Temp*X,Esp-1,Y).
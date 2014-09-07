function createSurfaceFile(x,y,z)

%This function takes a surface, defined by z(i,j) = z(x(i),y(j)), and
%writes it to a specially formatted data file for reading into Java

%It is assumed that x and y are monotonically increasing...
%
% x = linspace(xLow,xUpp,nx);
% y = linspace(yLow,yUpp,ny);
% [xx,yy] = meshgrid(x,y);
% z = f(x,y)
%

%Figure out the dimensions and do basic error checking
[ny,nx] = size(z);
if (length(x)~=nx), error('Invalid input!'); end;
if (length(y)~=ny), error('Invalid input!'); end;

xLow = x(1); xUpp = x(end);
yLow = y(1); yUpp = y(end);

fid = fopen('../examples/surface.txt','w');

fprintf(fid,'xLow %.16f\n',xLow);
fprintf(fid,'xUpp %.16f\n',xUpp);
fprintf(fid,'yLow %.16f\n',yLow);
fprintf(fid,'yUpp %.16f\n',yUpp);
fprintf(fid,'nx %d\n',nx);
fprintf(fid,'ny %d\n',ny);

for j=1:ny
    for i=1:nx
        fprintf(fid,'%.16f ',z(j,i));
    end
    fprintf(fid,'\n');
end

fclose(fid);

end
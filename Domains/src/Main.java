import java.util.*;

public class Main {
    public static void main(String[] args)
    {
        double v=0.5;           // v = [0.1 .. 0.9]. вероятность выпадения 1. по умолчанию 0.5 (50%).
        int l=3, m=5, n=7;    // размеры решетки
        int h=l+m-2;            // высота решетки
        int w=n+m-1;            // ширина решетки
        int numDom=0;           // количество всех доменов в матрице
        int numDomNonSingl=0;   // количество неодносвязных доменов

        int[][] grid = new int[h+1][w];               //массив значений 0/1 (решетка)
        boolean[][] mark = new boolean[h+1][w];       //вспомогательный массив. Используется для поиска доменов

        //формируем решетку

        Random random = new Random();
        for (int y = 0; y <= h ; y++) {
            for (int x = 0; x < w; x++) {
                if (((y < l) & (x < n + y)) || ((y >= l) & (x >= y - l + 1) & (x < n+y))) {
                    if (random.nextDouble() < v) grid[y][x] = 1;
                    else grid[y][x] = 0;
                } else {
                    grid[y][x] = 2;
                }
            }
        }


        // маркируем все ячейки с 0 которые касаются границы решетки
        // требуется для поиска неодносвязных доменов

        for (int x = 0; x < n; x++) {
            creatDom(0, 0, x, grid, mark);
        }

        for (int x = m-1; x < w; x++) {
            creatDom(0, h, x, grid, mark);
        }

        int xx=0, xxx=n-1;
        for (int y = 1; y <  h; y++) {
            if (y>=l) xx++;
            if (y<m-1) xxx++;
            creatDom(0, y, xx, grid, mark);
            creatDom(0, y, xxx, grid, mark);
        }

        // подсчет односвязных и неодносвязных доменов
        List<YX> lstYX=new ArrayList<>();
        List<YX> yxNonSingl=new ArrayList<>();
        for (int y = 0; y <= h ; y++) {

            for (int x = 0; x < w; x++) {
                lstYX=creatDom(1,y,x,grid,mark);
                if (!lstYX.isEmpty()) numDom++;

                // проверка домена
                // перебираем элементы домена и проверяем наличие возле них элемента с 0 и не маркированного
                // Если такой элемент есть, то этот домен является неодносвязным

                if (lstYX.size()>5) {       //если в домене меньше 6 элементов, то он не может быть неодносвязным
                    for (YX yx : lstYX) {
                        if (!getListNeighbours(0, yx.getY(), yx.getX(), grid, mark).isEmpty()) {
                            yxNonSingl.addAll(lstYX);
                            numDomNonSingl++;
                            break;
                        }
                    }
                }
            }
        }

        // вывод результата в консоль
        //для наглядности неодносвязные домены отмечаем 5
        for (YX yx: yxNonSingl){
            grid[yx.getY()][yx.getX()] = 5;
        }

        String s="";
        for (int y = 0; y <= h ; y++) {
            if (y<l) s=" ".repeat(l-y); else s=" ";
            System.out.printf("%3d  %s\n", y, s + Arrays.toString(grid[y]).replaceAll("[\\[\\],]", "").replaceAll("2",""));
        }
        System.out.println();
        System.out.println("количество доменов - " + numDom);
        System.out.println("количество неодносвязных доменов - " + numDomNonSingl);
    }

    static List<YX> creatDom(int z, int y, int x, int[][] gr, boolean[][] mark){
        List<YX> yx = new LinkedList<>();
            if (!mark[y][x] && gr[y][x]==z) {
                mark[y][x]=true;
                yx.add(new YX (y,x));
                for (int i = 0; i < yx.size() ; i++) {
                    yx.addAll(getListNeighbours(z, yx.get(i).getY(), yx.get(i).getX(), gr,mark));
                }
            }
        return yx;
    }
    static List<YX> getListNeighbours(int z, int y, int x, int[][] gr, boolean[][] mark) {
        List<YX> yx = new LinkedList<>();
        if ((y>0 && x>0) && (!mark[y-1][x-1] && gr[y-1][x-1]==z && gr[y-1][x-1]!=2)) yx.add(new YX(y-1,x-1));
        if (y>0 && (!mark[y-1][x] && gr[y-1][x]==z && gr[y-1][x]!=2)) yx.add(new YX(y-1,x));
        if (x>0 && (!mark[y][x-1] && gr[y][x-1]==z && gr[y][x-1]!=2)) yx.add(new YX(y,x-1));
        if (x<gr[y].length-1 && (!mark[y][x+1] && gr[y][x+1]==z && gr[y][x+1]!=2)) yx.add(new YX(y,x+1));
        if (y<gr.length-1 && (!mark[y+1][x] && gr[y+1][x]==z && gr[y+1][x]!=2)) yx.add(new YX(y+1,x));
        if ((y<gr.length-1 && x<gr[y].length-1) && (!mark[y+1][x+1] && gr[y+1][x+1]==z && gr[y+1][x+1]!=2)) yx.add(new YX(y+1,x+1));
        for (YX e: yx) {
            mark[e.getY()][e.getX()]=true;
        }
        return yx;
    }

    static class YX{
        int x,y;
        public YX (int y, int x){
            this.y=y;
            this.x=x;
        }
         public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        @Override
        public String toString(){
            return y + ":" + x;
        }
    }
}
public class texture {
    private int Width;
    private int Height;

    //масив кольорів, покищо представлений у вигляді цілого числа
    private int[] Texels;

    //кольор який буде повертатись у вападку виходу за межі текстури
    private int AlternateColor = 0XFF00FF00;

    //Колір границі для білінійоної інтерполяції
    private int BorderColor = 0XFF000000;

    //тип семплінгу текстури
    private sampler Sampler = sampler.SAMPLER_TYPE_BILINEAR;

    //дефолтний коструктор, створює текстуру шахової дошки
    texture () {
        int BlocSize = 8;
        int NumBloc = 32;

        this.Width = BlocSize * NumBloc;
        this.Height = BlocSize * NumBloc;

        this.Texels = new int[Width * Height];

        for (int y = 0; y < NumBloc; y++) {
            for (int x = 0; x < NumBloc; x++) {

                for (int BlocY = 0; BlocY < BlocSize; BlocY++) {
                    for (int BlocX = 0; BlocX < BlocSize; BlocX++) {
                        int position = (y * BlocSize + BlocY) * Width + (x * BlocSize + BlocX);

                        if ((x + y % 2) % 2 == 0) {
                            this.Texels[position] = 0xFFFFFFFF;
                        } else {
                            this.Texels[position] = 0xFF000000;
                        }
                    }
                }

            }
        }
    }


    //повертає колір відповідно до заданий кординат. Кординати повинні бути в межах 0...1
    public int getColor(float X, float Y) {
       return Sampler.getColor(this, X, Y);
    }

    public int getColor(vec2D Point) {
        return Sampler.getColor(this, Point.x(), Point.y());
    }

    private int ColorRgbToInt(vec4D Color) {
        return ((int)(Color.a() * 255) << 24) | ((int)(Color.r() * 255) << 16) | ((int)(Color.g() * 255) << 8) | (int)(Color.b() * 255);
    }

    private vec4D ColorIntToRgb(int color) {
        return new vec4D(
                ((color & 0X00FF0000) >> 16) / 255.f,
                ((color & 0x0000FF00) >> 8) / 255.f,
                (color & 0x000000FF) / 255.f,
                ((color & 0XFF000000) >> 24) / 255.f
        );
    }

    public enum sampler {
        SAMPLER_TYPE_NEAR{
            @Override
            public int getColor(texture Texture, float X, float Y) {
                if (X >= 0 && X <= 1 && Y >= 0 && Y <= 1) {
                    int TexelX = (int) Math.floor(X * (Texture.Width - 1));
                    int TexelY = (int) Math.floor(Y * (Texture.Height - 1));
                    return (Texture.Texels[TexelY * Texture.Width + TexelX]);
                } else {
                    return Texture.AlternateColor;
                }
            }
        },

        SAMPLER_TYPE_BILINEAR {
            @Override
            public int getColor(texture Texture, float X, float Y) {
                //Положення пікселяна в корддинатах техтури
                float PointX = X * Texture.Width - 0.5f;
                float PointY = Y * Texture.Height - 0.5f;

                //Пошук кордина найблищого теселю з ліва з низу
                int TexelX = (int) Math.floor(PointX);
                int TexelY = (int) Math.floor(PointY);

                // масив кординат найблищих текселів, порядок нижній лівий, нижній правий, верхній лівий, верхній правий
                vec2D[] TexelPosition = new vec2D[] {
                        new vec2D(TexelX, TexelY),
                        new vec2D(TexelX + 1, TexelY),
                        new vec2D(TexelX, TexelY + 1),
                        new vec2D(TexelX + 1, TexelY + 1),
                };

                //масив кольорів найблищих текселів, порядок той самий що і для кординат
                vec4D[] TexelColor = new vec4D[4];


                for (int i = 0; i < TexelPosition.length; i++) {
                    if (TexelPosition[i].x() >= 0 && TexelPosition[i].x() < Texture.Width &&
                            TexelPosition[i].y() >= 0 && TexelPosition[i].y() < Texture.Height) {
                        TexelColor[i] = Texture.ColorIntToRgb(Texture.Texels[
                                        (int)(TexelPosition[i].y() * Texture.Width + TexelPosition[i].x())]);
                    } else {
                        TexelColor[i] = Texture.ColorIntToRgb(Texture.BorderColor);
                    }
                }

                //параметри інтерполяції, горизотальні s та вертикальні k
                float S = PointX - TexelX;
                float K = PointY - TexelY;

                vec4D LowInterpolateColor = TexelColor[0].mult(1.f - S).add(
                        TexelColor[1].mult(S)
                );
                vec4D UpInterpolateColor = TexelColor[2].mult(1.f - S).add(
                        TexelColor[3].mult(S)
                );

                vec4D InterpolateColor =LowInterpolateColor.mult(1.f - K).add(
                        UpInterpolateColor.mult(K)
                );

                return Texture.ColorRgbToInt(InterpolateColor);
            }
        };

        public abstract int getColor(texture Texture, float X, float Y);
    }
}



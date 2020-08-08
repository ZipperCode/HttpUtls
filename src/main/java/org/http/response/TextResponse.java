package org.http.response;

import java.io.Serializable;

public class TextResponse implements Serializable {

    private SlidesShow slidesShow;

    public class SlidesShow{
        private String author;
        private String date;
        private String title;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "SlidesShow{" +
                    "author='" + author + '\'' +
                    ", date='" + date + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    public SlidesShow getSlidesShow() {
        return slidesShow;
    }

    @Override
    public String toString() {
        return "TextResponse{" +
                "slidesShow=" + slidesShow +
                '}';
    }

    public void setSlidesShow(SlidesShow slidesShow) {
        this.slidesShow = slidesShow;
    }
}

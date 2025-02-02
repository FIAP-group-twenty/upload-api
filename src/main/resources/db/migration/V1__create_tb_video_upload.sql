create table tb_video_upload(
    id int primary key auto_increment,
    email varchar(100) not null,
    title varchar(100) not null,
    url_video varchar(200),
    url_zip_image varchar(200),
    status varchar(200),
    created_at timestamp,
    updated_at timestamp
)
create table if not exists adp_mock_spr_src.achievement_process
(
    id            int auto_increment
        primary key,
    seller_id     int                     null,
    target_name   varchar(255)            null,
    target_amount decimal(18, 2)          null,
    target_unit   varchar(10) default '万' null,
    start_time    date                    null,
    end_time      date                    null,
    finish_amount decimal(18, 2)          null,
    finish_unit   varchar(10) default '元' null,
    finish_time   datetime                null
);

create index achievement_process_seller_id_index
    on adp_mock_spr_src.achievement_process (seller_id);

create table if not exists adp_mock_spr_src.customer
(
    id        bigint auto_increment
        primary key,
    real_name varchar(255)  not null,
    id_card   char(18)      not null,
    gender    int           not null comment '0: 女性，1: 男性',
    mobile    varchar(11)   null,
    address   varchar(2000) null,
    company   varchar(255)  null comment '公司地址'
);

create table if not exists adp_mock_spr_src.customer_account_info
(
    id             bigint auto_increment
        primary key,
    customer_id    bigint             not null,
    account_no     varchar(255)       null comment '账户号',
    risk_level     smallint default 1 null,
    risk_test_date date               null,
    account_bank   int                null,
    entry_time     date               null comment '开户时间',
    loc_seller     bigint             null comment '客户经理',
    account_level  int      default 1 null,
    constraint customer_account_info_customer_id_fk
        foreign key (customer_id) references adp_mock_spr_src.customer (id)
);

create index customer_account_info_customer_id_index
    on adp_mock_spr_src.customer_account_info (customer_id);

create table if not exists adp_mock_spr_src.customer_seller_relation
(
    id              int auto_increment
        primary key,
    customer_id     int                  null,
    seller_id       int                  null,
    relation_type   int        default 1 null,
    wechat_relation tinyint(1) default 0 null
)
    comment '客户关系';

create table if not exists adp_mock_spr_src.customer_statistics
(
    id            int auto_increment
        primary key,
    customer_id   int            null,
    subject       varchar(255)   null,
    customer_type int default 1  null,
    balance       decimal(18, 2) null,
    l_date        date           null
);

create index customer_statistics_customer_id_index
    on adp_mock_spr_src.customer_statistics (customer_id);

create table if not exists adp_mock_spr_src.customer_transaction
(
    id                     bigint auto_increment
        primary key,
    customer_id            int            null,
    deal_partner_account   varchar(255)   null,
    deal_partner_name      varchar(256)   null,
    deal_partner_org_num   varchar(255)   null,
    deal_partner_ogr_name  varchar(255)   null,
    transaction_time       datetime       null,
    subject                varchar(255)   null,
    comment                varchar(1000)  null,
    balance                decimal(18, 2) null,
    deal_account_type_code varchar(255)   null,
    deal_code              varchar(255)   null,
    deal_abstract_code     varchar(255)   null,
    transaction_amount     decimal(18, 2) null,
    borrow_loan            int            null comment '1: 出账，2：入账',
    customer_account_id    bigint         null
);

create index customer_transaction_customer_id_index
    on adp_mock_spr_src.customer_transaction (customer_id);

create table if not exists adp_mock_spr_src.department
(
    id              int auto_increment
        primary key,
    department_name varchar(1000)        not null,
    parent_id       int                  not null comment '上级部门，-1为根部门',
    principal_id    int                  null comment '负责人id',
    type_id         smallint             null comment '部门类型（0: 总行，1：分行，2：支行，3：网点，4：普通部门）',
    tenant_id       int                  null comment '租户id',
    address         varchar(255)         null comment '地址',
    is_leaf         tinyint(1) default 0 null comment '是否为最基层的部门'
);

create table if not exists adp_mock_spr_src.product_deal_data
(
    id                          bigint auto_increment
        primary key,
    customer_id                 int                           null,
    seller_id                   int                           null,
    deal_time                   datetime                      null,
    product_id                  int                           null,
    production_real_arrive_date date                          null,
    amount                      decimal(18, 2)                null,
    is_pre_end                  tinyint(1)  default 0         null,
    is_transfer                 tinyint(1)  default 0         null,
    transfer_time               datetime                      null,
    branch_bank                 int                           null,
    opration_method             varchar(20) default 'default' null,
    is_auto_transfer_save       int                           null
);

create table if not exists adp_mock_spr_src.production
(
    id               int auto_increment
        primary key,
    name             varchar(255)                 null,
    product_type     varchar(10)                  null comment '产品类型',
    product_tag      varchar(255)                 null comment '产品标签',
    detail           varchar(1000)                null,
    term             varchar(100)                 null comment '产品期限',
    rate             decimal(12, 2)               null comment '产品利率',
    is_float_rate    tinyint(1)     default 0     null comment '是否浮动利率',
    interest_method  smallint                     null comment '付息方式',
    min_amount       decimal(18, 2)               null,
    max_amount       decimal(18, 2)               null,
    can_pre_end      tinyint(1)     default 0     null comment '是否可以提前结束',
    can_transfer     tinyint(1)                   null,
    can_pledge       tinyint(1)                   null,
    risk_level       smallint       default 1     null,
    start_date       date                         null,
    end_date         date                         null,
    build_date       date                         null,
    due_date         date                         null,
    real_arrive_date varchar(10)    default 'T+2' null,
    trusteeship_fee  decimal(18, 2) default 0     null,
    fix_manage_fee   decimal(18, 2) default 0     null,
    float_manage_fee decimal(18, 2) default 0     null,
    year_income_rate decimal(12, 2)               null,
    history_income   decimal(18, 2)               null
);

create table if not exists adp_mock_spr_src.seller_info
(
    id            bigint auto_increment
        primary key,
    id_card       char(18)      not null,
    current_bank  int           not null,
    entry_time    date          null,
    name          varchar(255)  null,
    email         varchar(1000) null,
    mobile        char(11)      null,
    leader_id     int           null,
    position      int           null comment '职位（1：基层，2：中层，3：高层）',
    tenant_id     int           null,
    department_id smallint      null,
    constraint seller_info_department_id_fk
        foreign key (current_bank) references adp_mock_spr_src.department (id),
    constraint seller_info_department_id_fk_2
        foreign key (department_id) references adp_mock_spr_src.department (id)
);



truncate table adp_mock_spr_src.achievement_process;
truncate table adp_mock_spr_src.customer;
truncate table adp_mock_spr_src.customer_account_info;
truncate table adp_mock_spr_src.customer_seller_relation;
truncate table adp_mock_spr_src.customer_statistics;
truncate table adp_mock_spr_src.customer_transaction;
truncate table adp_mock_spr_src.department;
truncate table adp_mock_spr_src.product_deal_data;
truncate table adp_mock_spr_src.production;
truncate table adp_mock_spr_src.seller_info;

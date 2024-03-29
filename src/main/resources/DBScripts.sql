ALTER TABLE R365.group_user 
DROP FOREIGN KEY FK6u7jb50qa69gr3505uttxm86x,
DROP FOREIGN KEY FKhm9ocxsgn3u36fjr4555mcwjo,
DROP FOREIGN KEY FKm4p7t99vp509n4lt2et6hqkgn,
DROP FOREIGN KEY FKn6qa4x71ygqenb0kirnkxd5jm,
DROP FOREIGN KEY FKooi2c70hs1rbqa8sbpsytdxwh;
ALTER TABLE R365.group_user 
ADD CONSTRAINT FK6u7jb50qa69gr3505uttxm86x
  FOREIGN KEY (user_id)
  REFERENCES R365.user (user_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT FKhm9ocxsgn3u36fjr4555mcwjo
  FOREIGN KEY (group_role_id)
  REFERENCES R365.group_role (group_role_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT FKm4p7t99vp509n4lt2et6hqkgn
  FOREIGN KEY (group_id)
  REFERENCES R365.groups (group_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT FKn6qa4x71ygqenb0kirnkxd5jm
  FOREIGN KEY (last_modified_by_id)
  REFERENCES R365.user (user_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT FKooi2c70hs1rbqa8sbpsytdxwh
  FOREIGN KEY (created_by_id)
  REFERENCES R365.user (user_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;


ALTER TABLE R365.groups 
DROP FOREIGN KEY FK2et2tl22e96pckikovdy3py70,
DROP FOREIGN KEY FK7pmbdcwdxmeuns4loone2nbai,
DROP FOREIGN KEY FKdfl8mmj07v7e38ub4oo8urp5n;
ALTER TABLE R365.groups 
ADD CONSTRAINT FK2et2tl22e96pckikovdy3py70
  FOREIGN KEY (last_modified_by_id)
  REFERENCES R365.user (user_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT FK7pmbdcwdxmeuns4loone2nbai
  FOREIGN KEY (module_type_id)
  REFERENCES R365.module_type (Module_Type_Id)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT FKdfl8mmj07v7e38ub4oo8urp5n
  FOREIGN KEY (created_by_id)
  REFERENCES R365.user (user_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

ALTER TABLE R365.group_role 
DROP FOREIGN KEY FK11m4emsw0t4m5jotn35c0vyhe,
DROP FOREIGN KEY FK223x2nim95nxivelb4pqg7hu,
DROP FOREIGN KEY FK71xlt8durutjwkmgcuwvwd7ev;
ALTER TABLE R365.group_role 
ADD CONSTRAINT FK11m4emsw0t4m5jotn35c0vyhe
  FOREIGN KEY (created_by_id)
  REFERENCES R365.user (user_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT FK223x2nim95nxivelb4pqg7hu
  FOREIGN KEY (last_modified_by_id)
  REFERENCES R365.user (user_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT FK71xlt8durutjwkmgcuwvwd7ev
  FOREIGN KEY (group_id)
  REFERENCES R365.groups (group_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;


ALTER TABLE R365.group_role_has_action_type 
DROP FOREIGN KEY FKl4iegi2um0ip35555cjewi7et,
DROP FOREIGN KEY FKocsmylwp6h0rxygifa66qpewk,
DROP FOREIGN KEY FKoi1olrny94821nk24jupar3bu,
DROP FOREIGN KEY FKqryefcoltvb3ciqq9w1ttgekg;
ALTER TABLE R365.group_role_has_action_type 
ADD CONSTRAINT FKl4iegi2um0ip35555cjewi7et
  FOREIGN KEY (action_type_id)
  REFERENCES R365.action_type (action_type_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT FKocsmylwp6h0rxygifa66qpewk
  FOREIGN KEY (last_modified_by_id)
  REFERENCES R365.user (user_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT FKoi1olrny94821nk24jupar3bu
  FOREIGN KEY (created_by_id)
  REFERENCES R365.user (user_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT FKqryefcoltvb3ciqq9w1ttgekg
  FOREIGN KEY (group_role_id)
  REFERENCES R365.group_role (group_role_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;



ALTER TABLE  contract 
DROP FOREIGN KEY FK24c7jjdj3o7eils20160yy8ng;
ALTER TABLE  contract 
ADD CONSTRAINT FK24c7jjdj3o7eils20160yy8ng
  FOREIGN KEY (parent_contract_id)
  REFERENCES  contract (contract_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
  
  ALTER TABLE R365.contract_reviewer 
DROP FOREIGN KEY FK9ftk7mj260hdt2u6ab7s5hco5;
ALTER TABLE R365.contract_reviewer 
ADD CONSTRAINT FK9ftk7mj260hdt2u6ab7s5hco5
  FOREIGN KEY (contract_id)
  REFERENCES R365.contract (contract_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
  
  
  ALTER TABLE R365.contract_has_status 
DROP FOREIGN KEY FKbo8q4q45wcf3s4ogjyfcwvd5r,
DROP FOREIGN KEY FKj8i9phk9lkc9oageeb3duutpe;
ALTER TABLE R365.contract_has_status 
ADD CONSTRAINT FKbo8q4q45wcf3s4ogjyfcwvd5r
  FOREIGN KEY (contract_id)
  REFERENCES R365.contract (contract_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT FKj8i9phk9lkc9oageeb3duutpe
  FOREIGN KEY (contract_status_id)
  REFERENCES R365.contract_status (Contract_Status_Id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

  ALTER TABLE R365.asset_type 
DROP FOREIGN KEY FKaeqvioptftuuy7iyhvi74dekk;
ALTER TABLE R365.asset_type 
ADD CONSTRAINT FKaeqvioptftuuy7iyhvi74dekk
  FOREIGN KEY (parent_asset_type_id)
  REFERENCES R365.asset_type (asset_type_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

INSERT INTO R365.action_type (action_type_id, name) VALUES ('10', 'NotificationCC(Last Reminder)');
INSERT INTO R365.action_type (action_type_id, name) VALUES ('11', 'NotificationCC(Expiry Reminder)');
#INSERT INTO R365.action_type (action_type_id, name) VALUES ('12', 'AddtionalCC(Last Reminder)');
#INSERT INTO R365.action_type (action_type_id, name) VALUES ('13', 'AddtionalCC(Expiry Reminder)');


ALTER TABLE R365.action_type 
ADD COLUMN display_name VARCHAR(255) NULL DEFAULT NULL AFTER name;

ALTER TABLE R365.action_type 
ADD COLUMN admin_action BIT NULL AFTER display_name;


UPDATE R365.action_type SET display_name='Search' WHERE action_type_id='1';
UPDATE R365.action_type SET display_name='Download' WHERE action_type_id='2';
UPDATE R365.action_type SET display_name='View' WHERE action_type_id='3';
UPDATE R365.action_type SET display_name='Create' WHERE action_type_id='4';
UPDATE R365.action_type SET display_name='Update' WHERE action_type_id='5';
UPDATE R365.action_type SET display_name='Delete' WHERE action_type_id='6';
UPDATE R365.action_type SET display_name='Verify' WHERE action_type_id='7';
UPDATE R365.action_type SET display_name='TO' WHERE action_type_id='8';
UPDATE R365.action_type SET display_name='CC' WHERE action_type_id='9';
UPDATE R365.action_type SET display_name='CC (Last)' WHERE action_type_id='10';
UPDATE R365.action_type SET display_name='CC (Expiry)' WHERE action_type_id='11';



UPDATE R365.action_type SET admin_action=1 WHERE action_type_id='1';
UPDATE R365.action_type SET admin_action=1 WHERE action_type_id='2';
UPDATE R365.action_type SET admin_action=1 WHERE action_type_id='3';
UPDATE R365.action_type SET admin_action=1 WHERE action_type_id='4';
UPDATE R365.action_type SET admin_action=1 WHERE action_type_id='5';
UPDATE R365.action_type SET admin_action=1 WHERE action_type_id='6';
UPDATE R365.action_type SET admin_action=1 WHERE action_type_id='7';
UPDATE R365.action_type SET admin_action=1 WHERE action_type_id='8';
UPDATE R365.action_type SET admin_action=0 WHERE action_type_id='9';
UPDATE R365.action_type SET admin_action=0 WHERE action_type_id='10';
UPDATE R365.action_type SET admin_action=0 WHERE action_type_id='11';

ALTER TABLE R365.user 
ADD COLUMN department_id INT NOT NULL AFTER last_unsucessfull_login_at;

ALTER TABLE R365.user 
CHANGE COLUMN mobile_number mobile_number VARCHAR(15) NULL DEFAULT NULL ;

ALTER TABLE R365.location ADD UNIQUE INDEX location_name_UNIQUE (location_name ASC);

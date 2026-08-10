-- Manual rollback. This permanently removes P0 forum content data.
DROP TABLE IF EXISTS `forum_reaction`;
DROP TABLE IF EXISTS `forum_comment`;
DROP TABLE IF EXISTS `forum_post`;
DROP TABLE IF EXISTS `forum_circle`;

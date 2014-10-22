# == Schema Information
#
# Table name: last_modifieds
#
#  id                     :integer          not null, primary key
#  last_modified_datetime :datetime
#  created_at             :datetime
#  updated_at             :datetime
#

class LastModified < ActiveRecord::Base
end

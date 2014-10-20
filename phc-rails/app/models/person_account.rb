# == Schema Information
#
# Table name: person_accounts
#
#  id         :integer          not null, primary key
#  first_name :string(255)
#  last_name  :string(255)
#  birthday   :datetime
#  created_at :datetime
#  updated_at :datetime
#  sf_id      :string(255)
#

class PersonAccount < ActiveRecord::Base
end

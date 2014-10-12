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
#

class PersonAccount < ActiveRecord::Base
end

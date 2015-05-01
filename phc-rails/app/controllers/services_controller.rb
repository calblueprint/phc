class ServicesController < ApplicationController

  def show
    respond_with Service.services.sort.to_json
  end

end
